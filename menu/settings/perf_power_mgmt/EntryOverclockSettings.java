package menu.settings.perf_power_mgmt;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.ActionPanel;
import menu.action_panel.ActionableElement;
import system.PanelManager;

class EntryOverclockSettings extends MenuEntry {
    
    // TODO: is a submenu; needs a submenu
    public EntryOverclockSettings(MenuOptionList parent) {
        super(parent, "Overclocking Settings", Color.white, 12);
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new ActionPanel("Overclocking not supported",
            new ActionableElement() {
                protected void trigger() {
                    PanelManager.getPanelManager().popPanel();
                }
            }
        ));
    }
}
