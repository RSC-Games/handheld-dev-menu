package system;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import backend.TitleLaunchService;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Rumbler;
import net.java.games.input.Version;
import util.Log;
import util.Utils;

/**
 * Reports both key and gamepad button presses, and groups them into
 * associated sets.
 */
public class InputManager implements KeyListener, NativeKeyListener {
    private static final boolean ENABLE_MAPPING_MODE = false;
    private static final int RESCAN_INTERVAL = 60; // frames
    static InputManager activeManager;

    Controller activeController = null;
    ControllerLayout mapping = null;
    int rescanCounter = 0;
    ArrayList<Integer> releasedKeys;
    ArrayList<Integer> activeKeys;

    // TODO: OSK interface. OSKs are kinda weird and for maximum app compatibility I need to adhere to existing standards.
    // For now I'll use onboard but I will be writing a custom OSK later.
    // https://github.com/onboard-osk/onboard/blob/main/DBUS.md (dbus api for onboard)
    // https://stackoverflow.com/questions/28874365/how-to-make-a-qinputdialog-trigger-the-virtual-keyboard-to-show-up (dbus stuff)
    // https://github.com/onboard-osk/onboard/blob/main/Onboard/AutoShow.py (for autoshowing the keyboard in other apps)

    InputManager() {
        if (activeManager != null)
            throw new IllegalStateException("input manager already created!");
        
        activeManager = this;

        Log.logInfo("input: Dev Menu: JInput version " + Version.getVersion());
        GlobalScreen.addNativeKeyListener(this);

        // Set up oneshot key buffers.
        releasedKeys = new ArrayList<>();
        activeKeys = new ArrayList<>();

        if (!connectController())
            Log.logWarning("input: failed to connect/map controller");
    }

    /**
     * JInput forces a default log verbosity of INFO, which spits out some annoying logs.
     */
    private void forceDisableJInputLogs() {
        Package pkg = ControllerEnvironment.class.getClassLoader().getDefinedPackage("net.java.games.input");
        Logger hLog = Logger.getLogger(pkg.getName());
        hLog.setLevel(Level.SEVERE);
    }

    public static InputManager getInputManager() {
        return activeManager;
    }

    // Main use of this class: Determine if specific button groups, called "actions",
    // are active.
    public boolean confirm() {
        if (ENABLE_MAPPING_MODE || !isControllerAttached())
            return getKeyUp(KeyEvent.VK_ENTER);
        return getKeyUp(KeyEvent.VK_ENTER) || this.mapping.getButtonA();
    }

    public boolean back() {
        if (ENABLE_MAPPING_MODE || !isControllerAttached())
            return getKeyUp(KeyEvent.VK_ESCAPE);
        return getKeyUp(KeyEvent.VK_ESCAPE) || this.mapping.getButtonB();
    }

    public boolean edit() {
        if (ENABLE_MAPPING_MODE || !isControllerAttached())
            return getKeyUp(KeyEvent.VK_E);
        return getKeyUp(KeyEvent.VK_E) || this.mapping.getButtonX();
    }

    public boolean moveLeft() {
        if (ENABLE_MAPPING_MODE || !isControllerAttached())
            return getKey(KeyEvent.VK_LEFT);
        return getKey(KeyEvent.VK_LEFT) || this.mapping.getDPADLeft() || this.mapping.getLeftStick()[0] < 0;
    }

    public boolean moveUp() {
        if (ENABLE_MAPPING_MODE || !isControllerAttached())
            return getKey(KeyEvent.VK_UP);
        return getKey(KeyEvent.VK_UP) || this.mapping.getDPADUp() || this.mapping.getLeftStick()[1] < 0;
    }

    public boolean moveRight() {
        if (ENABLE_MAPPING_MODE || !isControllerAttached())
            return getKey(KeyEvent.VK_RIGHT);
        return getKey(KeyEvent.VK_RIGHT) || this.mapping.getDPADRight() || this.mapping.getLeftStick()[0] > 0;
    }

    public boolean moveDown() {
        if (ENABLE_MAPPING_MODE || !isControllerAttached())
            return getKey(KeyEvent.VK_DOWN);
        return getKey(KeyEvent.VK_DOWN) || this.mapping.getDPADDown() || this.mapping.getLeftStick()[1] > 0;
    }

    public int getXAxis() {
        return (moveRight() ? 1 : 0) - (moveLeft() ? 1 : 0); 
    }

    public int getYAxis() {
        return (moveUp() ? 1 : 0) - (moveDown() ? 1 : 0);
    }

    /**
     * Pulse all rumble motors in the controller for the given intensity.
     * Called shock due to how it feels.
     * 
     * @param ms
     * @param intensity
     */
    // Probably should do the rumble in the layout system.
    void shock(int ms, float intensity) {
        if (ENABLE_MAPPING_MODE || !isControllerAttached())
            return;

        Rumbler[] rumblers = activeController.getRumblers();

        if (rumblers.length == 0)
            Log.logInfo("input: no rumblers detected");
        
        for (Rumbler rumbler : rumblers) 
            rumbler.rumble(intensity);

        Utils.sleepms(ms);

        for (Rumbler rumbler : rumblers)
            rumbler.rumble(0f);
    }

    /**
     * Find a controller and connect the first one presented, if any.
     * Pointers will be updated automatically within the function.
     * 
     * @implNote Cannot be used to remap disconnected controllers due to lack of support
     *  in JInput.
     * @return Whether a controller could be mapped.
     */
    boolean connectController() {
        ControllerEnvironment defaultEnviron = ControllerEnvironment.getDefaultEnvironment();
        eraseControllersField(defaultEnviron);

        // Disable logging w/o environment variable (kinda annoying)
        forceDisableJInputLogs();

        // Some controllers present multiple input types, so find gamepad first.
        activeController = findControllerByType(Controller.Type.GAMEPAD);

        if (activeController == null)
            activeController = findControllerByType(Controller.Type.STICK);

        if (activeController == null)
            return false;

        // We have a controller.
        this.mapping = ControllerLayout.getMapping(activeController);

        // Won't be using a mapping.
        if (ENABLE_MAPPING_MODE)
            return true;

        // Unable to map the controller for some reason?
        if (this.mapping == null) {
            Log.logInfo("input: unable to map gamepad; disabling input");
            activeController = null;
            return false;
        }

        shock(100, 1f);
        return true;
    }

    private Controller findControllerByType(Controller.Type type) {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for (Controller controller : controllers) {
            //System.out.println("found input device " + controller.getName() + " type " + controller.getType()); 
            if (controller.getType().equals(type))
                return controller;
        }

        return null;
    }

    /**
     * Unlock and erase the internal controllers field to force JInput to rescan for input devices.
     * Shouldn't be done technically but without official ways to rescan it'll have to be done
     * this way.
     * 
     * @param environment The environment to force rescan on.
     */
    private void eraseControllersField(ControllerEnvironment environment) {
        try {
            Field controllersField = environment.getClass().getDeclaredField("controllers");
            controllersField.setAccessible(true);
            controllersField.set(environment, null);

            // Reload plugins
            Field plugins = environment.getClass().getDeclaredField("loadedPluginNames");
            plugins.setAccessible(true);
            ((Collection<?>)plugins.get(environment)).clear();
        }
        catch (IllegalAccessException ie) {
            Log.logInfo("input: controller module: unable to unlock field");
        }
        catch (NoSuchFieldException ie) {
            Log.logInfo("input: controller module: cannot locate field to overwrite!");
        }
    }

    /**
     * Determine if a controller is present/installed
     * @return Whether a controller is present.
     */
    public boolean isControllerAttached() {
        return activeController != null;
    }

    public void poll() {
        this.releasedKeys.clear();

        // Would remap controller but JInput doesn't support reconnecting.
        if (!isControllerAttached()) {
            // Not ready to rescan yet/timer expired already
            if (rescanCounter-- != 0)
                return;

            // Rescan once every second.
            if (!connectController()) {
                rescanCounter = RESCAN_INTERVAL;
                return;
            }

            Log.logVerbose("input: controller reconnected");
        }

        if (!activeController.poll()) {
            Log.logVerbose("input: controller disconnected");
            rescanCounter = RESCAN_INTERVAL;
            activeController = null;
            mapping = null;
            return;
        }

        if (ENABLE_MAPPING_MODE) {
            ControllerLayout.printMappingInformation(activeController);
            return;
        }

        mapping.poll();

        // Determine if the home button was pressed (focus request)
        if (mapping.getButtonHome() && TitleLaunchService.getWindowHideState())
            reopenWindow();
    }

    private void reopenWindow() {
        TitleLaunchService.setWindowHideState(false);
    }

    boolean getKey(Integer code) {
        return this.activeKeys.contains((Integer)code);
    }

    boolean getKeyUp(Integer code) {
        return this.releasedKeys.contains((Integer)code);
    }

    // TODO: Determine short vs long home key press to open app menu/options overlay menu respectively
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_HOME)
            reopenWindow();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_HOME)
            reopenWindow();

        if (!TitleLaunchService.getWindowHideState() && !this.activeKeys.contains(e.getKeyCode()))
            this.activeKeys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (TitleLaunchService.getWindowHideState())
            return;

        this.activeKeys.remove((Integer)e.getKeyCode());
        this.releasedKeys.add(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {} // Stubbed
}
