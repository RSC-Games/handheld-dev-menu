package system;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Stack;

import ui.UIPanel;

public class PanelManager {
    private static PanelManager panelManager;
    Stack<UIPanel> panelStack;

    PanelManager() {
        if (panelManager != null)
            throw new IllegalStateException("panelmanager already created!");

        panelStack = new Stack<>();
        panelManager = this;
    }

    public static PanelManager getPanelManager() {
        return panelManager;
    }

    /**
     * Get all of the names in this stack for the navbar.
     * @return The path of this menu.
     */
    public String getPanelPath() {
        ArrayList<String> names = new ArrayList<>();
        panelStack.forEach((x)->names.add(x.getName()));
        return String.join("/", names);
    }

    /**
     * Push a new active panel.
     * 
     * @param panel
     */
    public void pushPanel(UIPanel panel) {
        panelStack.push(panel);
    }

    /**
     * Move down a panel in the menu system.
     */
    public void popPanel() {
        panelStack.pop();
    }

    /**
     * Removes all panels currently registered.
     */
    public void reset() {
        panelStack.clear();
    }

    /**
     * Determine if any panels remain to be updated.
     * 
     * @return True if any such panels remain.
     */
    public boolean hasPanels() {
        return panelStack.size() > 0;
    }

    /**
     * Force reset to the root panel.
     */
    public void jumpToRootPanel() {
        while (panelStack.size() > 1)
            panelStack.pop();
    }

    /**
     * Run the tick logic on the active panel.
     */
    public void updateTopPanel() {
        UIPanel top = panelStack.peek();
        top.update();
    }

    /**
     * Render the top panel.
     */
    public void drawTopPanel(Graphics g) {
        UIPanel top = panelStack.peek();
        top.drawAll(g);
    }
}
