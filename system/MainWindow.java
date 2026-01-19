package system;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;

import backend.CommandUtils;
import backend.CommandUtils.CommandOutput;
import util.Log;
import backend.TitleLaunchService;

// TODO: Overlay options window in a new class.
// TODO: Performance overlay (not interactible).
public class MainWindow extends JPanel {
    PanelManager panelManager;
    InputManager inputManager;
    boolean isHiding = false;
    JFrame frame;

    // Window ID for the app. Only present while an app is running, and is re-determined
    // every time the window is hidden.
    int appWindowID = -1;

    // Allows identifying if an issue has occurred during panel painting.
    volatile FailedRenderException exc;
    
    public MainWindow() {
        frame = new JFrame("menu_system");
        frame.setVisible(false);
        frame.setFocusable(true);

        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Resolution of the handheld
        frame.setSize(800, 480);
        frame.setLocation(0, 0);

        inputManager = new InputManager();
        frame.add(this);
        frame.addKeyListener(inputManager);

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
    public void display() {
        minimiseTitleWindow();

        frame.setAutoRequestFocus(true);
        int state = frame.getExtendedState();
        frame.setExtendedState(state & ~(JFrame.ICONIFIED));
        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();
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
        
        // TODO: Mask all x11 events.
        CommandUtils.executeCommandRetry("xdotool", "windowminimize", "" + this.appWindowID);
    }

    public void minimize() {
        int state = frame.getExtendedState();
        frame.setExtendedState(state | JFrame.ICONIFIED);
        //frame.setVisible(false);

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

    public void tickWindow() {       
        if (frame.isActive())
            frame.repaint();

        // Ran into an issue on this repaint. Abort.
        if (this.exc != null)
            throw exc;

        // Update window show/hide status
        TitleLaunchService.poll();
        boolean shouldStillHide = TitleLaunchService.getWindowHideState();

        // Prevent changing window state every frame.
        if (isHiding != shouldStillHide) {
            if (!shouldStillHide)
                display();
            else
                minimize();

            isHiding = shouldStillHide;
        }
    }

    /**
     * Determine if this window is currently visible or if it's been hidden.
     * 
     * @return If the window is visible.
     */
    public boolean isActive() {
        return frame.isActive();
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public void cleanUp() {
        minimize();
        frame.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        try {
            this.panelManager.drawTopPanel(g);
        }
        catch (Exception ie) {
            this.exc = new FailedRenderException(ie);
        }

        Toolkit.getDefaultToolkit().sync();
    }
}
