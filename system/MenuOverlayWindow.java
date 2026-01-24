package system;

import java.awt.Graphics;

import javax.swing.JFrame;

import menu.overlays.quick_settings.QuickSettingsOverlayMenu;
import ui.UIPanel;

public class MenuOverlayWindow extends WindowBase {
    UIPanel root;

    public MenuOverlayWindow() {
        super("settings_overlay", 0, 0, 250, 480, true, true, JFrame.DISPOSE_ON_CLOSE, 0);
        windowFrame.addKeyListener(InputManager.getInputManager());
        root = new QuickSettingsOverlayMenu();
    }

    @Override
    public boolean alwaysSimulate() {
        return false;
    }

    @Override
    protected void updateHook() {}

    @Override
    protected void tickContent() {
        root.update();
    }

    public void paintHook(Graphics g) throws Exception {
        root.drawAll(g);
    }
}
