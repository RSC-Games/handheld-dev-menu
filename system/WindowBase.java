package system;

import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class WindowBase extends JPanel {
    protected boolean isVisible = false;
    protected JFrame windowFrame;

    protected final int frameskip;
    private int frameskipCounter; 

    // Allows identifying if an issue has occurred during panel painting.
    volatile FailedRenderException exc;

    /**
     * Creates a window with the given properties. Adding a key listener is the responsibility of the
     * child class.
     * 
     * @param title Window title
     * @param x Global position on screen (x coordinate)
     * @param y Global position on screen (y coordinate)
     * @param w Window dimensions (x)
     * @param h Window dimensions (y)
     * @param alwaysOnTop Whether to force the window to render on top (usually desirable, required for overlays)
     * @param focusable Whether to allow focusing on the window in question
     * @param closeOp What to do on window close.
     * @param frameskip Number of frames to skip both tick and repaint.
     */
    protected WindowBase(String title, int x, int y, int w, int h, boolean alwaysOnTop, boolean focusable, 
                         int closeOp, int frameskip) {
        windowFrame = new JFrame();
        windowFrame.setVisible(false);
        windowFrame.setUndecorated(true);
        windowFrame.setFocusable(focusable);
        windowFrame.setAlwaysOnTop(alwaysOnTop);
        
        windowFrame.setDefaultCloseOperation(closeOp);
        windowFrame.setSize(w, h);
        windowFrame.setLocation(x, y);
        windowFrame.add(this);

        this.frameskip = frameskip;
    }

    /**
     * Determine if this window is currently visible or if it's been hidden.
     * 
     * @return If the window is visible.
     */
    public boolean isActive() {
        return windowFrame.isActive();
    }

    /**
     * Show the window and bring it to the front (so its visible)
     */
    public void show() {
        windowFrame.setAutoRequestFocus(true);
        windowFrame.setVisible(true);
        windowFrame.toFront();
        windowFrame.requestFocus();
    }

    /**
     * Hide the window
     */
    public void hide() {
        windowFrame.setVisible(false);
    }

    /**
     * Any window-specific update function should be called in here instead
     * of update.
     */
    protected abstract void updateHook();

    public abstract boolean alwaysSimulate();

    /**
     * Tick the window once the frameskip target has been met.
     */
    public final void tick() {
        // Updating the counter is deferred until the repaint since
        // otherwise repaint never occurs.
        if (frameskip != 0 && frameskipCounter != 0) 
            return;

        tickContent();
    }


    /**
     * Simulates the content within the window. Discrete step from window update
     * (which is mostly repainting).
     */
    protected abstract void tickContent();

    /**
     * Base window update.
     */
    public final void update() {
        // Counter update intentionally deferred until here.
        if (frameskip != 0 && frameskipCounter++ != 0) {
            frameskipCounter %= frameskip;
            return;
        }

        if (alwaysSimulate() || windowFrame.isActive())
            windowFrame.repaint();

        // Ran into an issue on this repaint. Abort.
        if (this.exc != null)
            throw exc;

        updateHook();
    }

    /**
     * Delete this surface and window.
     */
    public void destroy() {
        hide();
        windowFrame.dispose();
        windowFrame = null; // Catch later use after free errors
    }

    /**
     * Custom paint hook (exceptions are caught at the top level)
     * @param g Render target
     */
    protected abstract void paintHook(Graphics g) throws Exception;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        try {
            paintHook(g);
        }
        catch (Exception ie) {
            this.exc = new FailedRenderException(ie);
        }

        Toolkit.getDefaultToolkit().sync();
    }
}
