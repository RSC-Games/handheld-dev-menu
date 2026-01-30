package system;

import java.awt.Graphics;

import javax.swing.JFrame;

import menu.overlays.quick_settings.QuickSettingsOverlayMenu;
import ui.UIPanel;

public class MenuOverlayWindow extends WindowBase {
    private static MenuOverlayWindow theWindow;

    /**
     * UI Elements aren't loaded until the window is opened to save RAM.
     */
    UIPanel root = null;

    public MenuOverlayWindow() {
        super("settings_overlay", 0, 0, 265, 480, true, true, JFrame.DISPOSE_ON_CLOSE, 0);
        windowFrame.addKeyListener(InputManager.getInputManager());

        if (theWindow != null)
            throw new IllegalStateException("creating new menu overlay when one already exists!");

        theWindow = this;
    }

    public static MenuOverlayWindow getWindow() {
        return theWindow;
    }

    /**
     * Create all UI Elements on demand.
     */
    @Override
    public void show() {
        if (root == null) {
            root = new QuickSettingsOverlayMenu();
            super.show();
        }
        else {
            windowFrame.toFront();
            windowFrame.requestFocus();
        }
    }

    /**
     * Destroy all UI elements once they're no longer needed to decrease
     * RAM footprint.
     */
    @Override
    public void hide() {
        super.hide();
        root = null;
    }

    @Override
    public boolean alwaysSimulate() {
        return true;
    }

    @Override
    protected void updateHook() {}

    @Override
    protected void tickContent() {
        if (root == null)
            return;

        root.update();
    }

    public void paintHook(Graphics g) throws Exception {
        root.drawAll(g);
    }
}
