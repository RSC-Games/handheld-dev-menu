package system;

import java.awt.Graphics;

import javax.swing.JFrame;

import menu.overlays.perf_mon.PerfMonOverlay;
import ui.UIPanel;

public class PerfOverlayWindow extends WindowBase {
    UIPanel root;

    public PerfOverlayWindow() {
        super("performance_mon", 800-475, 0, 475, 15, true, false, JFrame.DISPOSE_ON_CLOSE, 60);
        windowFrame.addKeyListener(InputManager.getInputManager());
        root = new PerfMonOverlay();
    }

    /**
     * This window specifically we don't want to grab focus (or even process
     * input events since it's a HUD only window)
     */
    @Override
    public void show() {
        windowFrame.setVisible(true);
    }

    @Override
    protected void updateHook() {}

    /**
     * This window should ALWAYS be simulated regardless of whether it has
     * current focus or not (which it shouldn't have focus period)
     */
    @Override
    public boolean alwaysSimulate() {
        return true;
    }

    @Override
    protected void tickContent() {
        root.update();
    }

    @Override
    protected void paintHook(Graphics g) throws Exception {
        root.drawAll(g);
    }
    
}
