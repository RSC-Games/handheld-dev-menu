package menu.action_panel;

import system.PanelManager;

/**
 * Default action panel action. By default just pops the most recent UI panel.
 * Can be configured to pop more than one panel if desired.
 */
public class DefaultActionElement extends ActionableElement {
    int popPanelCount;

    public DefaultActionElement() {
        this.popPanelCount = 1;
    }

    public DefaultActionElement(int panels) {
        this.popPanelCount = panels;
    }

    @Override
    protected void trigger() {
        for (int i = 0; i < popPanelCount; i++)
            PanelManager.getPanelManager().popPanel();
    }
    
}
