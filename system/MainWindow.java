package system;
import java.awt.Graphics;

import javax.swing.JFrame;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;

import backend.CommandUtils;
import backend.CommandUtils.CommandOutput;
import util.Log;
import backend.TitleLaunchService;

// TODO: Overlay options window in a new class.
// TODO: Performance overlay (not interactible).
public class MainWindow extends WindowBase {
    PanelManager panelManager;
    InputManager inputManager;

    // Window ID for the app. Only present while an app is running, and is re-determined
    // every time the window is hidden.
    // TODO: Need to move window management to the title management service.
    int appWindowID = -1;
    
    public MainWindow() {
        super("menu_system", 0, 0, 800, 480, false, true, JFrame.EXIT_ON_CLOSE, 0);

        inputManager = new InputManager();
        windowFrame.addKeyListener(inputManager);

        panelManager = new PanelManager();
        GlobalScreen.setEventDispatcher(new SwingDispatchService());

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ie) {
            Log.logWarning("window: native hook load failed; not globally capturing keypresses");
        }
    }

    /**
     * Force the frame to the front and steal focus (if possible)
     */
    public void show() {
        minimiseTitleWindow();

        int state = windowFrame.getExtendedState();
        windowFrame.setExtendedState(state & ~(JFrame.ICONIFIED));
        super.show();
    }

    // TODO: Use C++ stub to get this working (can't get it working in Java)
    // https://gist.github.com/muktupavels/d03bb14ea6042b779df89b4c87df975d
    // Need to disable event handling for most X11 events (otherwise it'll continue to receive
    // gamepad events even when suspended).
    private void minimiseTitleWindow() {
        if (!TitleLaunchService.isTitleRunning()) {
            this.appWindowID = -1;
            return;
        }

        // Unfocus the application window manually.
        CommandOutput output = CommandUtils.executeCommandRetry("xdotool", "getwindowfocus");

        if (output.getExitCode() != 0)
            Log.logWarning("window: x11 couldn't identify title window for update");

        this.appWindowID = Integer.parseInt(output.getStdout().strip());
        
        // TODO: Mask all x11 events (will probably need C++ stub for this)
        CommandUtils.executeCommandRetry("xdotool", "windowminimize", "" + this.appWindowID);
    }

    public void hide() {
        int state = windowFrame.getExtendedState();
        windowFrame.setExtendedState(state | JFrame.ICONIFIED);

        maximiseTitleWindow();
    }

    private void maximiseTitleWindow() {
        // No window anymore.
        if (appWindowID == -1)
            return;

        CommandUtils.executeCommandRetry("xdotool", "windowmap", "" + this.appWindowID);
        // TODO: Unmask all x11 events.
        CommandUtils.executeCommandRetry("xdotool", "windowfocus", "" + this.appWindowID);
    }

    @Override
    protected void updateHook() {       
        // Update window show/hide status
        TitleLaunchService.poll();
        boolean shouldShow = !TitleLaunchService.getWindowHideState();

        // Prevent changing window state every frame.
        if (isVisible != shouldShow) {
            if (shouldShow)
                show();
            else
                hide();

            isVisible = shouldShow;
        }
    }

    @Override
    public boolean alwaysSimulate() {
        return false;
    }

    @Override
    protected void tickContent() {
        panelManager.updateTopPanel();
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    @Override
    protected void paintHook(Graphics g) throws Exception {
        this.panelManager.drawTopPanel(g);
    }
}
