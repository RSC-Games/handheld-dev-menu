package menu.overlays.quick_settings.quick_settings;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.DefaultActionElement;
import system.PanelManager;

class EntryOverclockSettings extends MenuEntry {
    
    // TODO: is a submenu; needs a submenu
    public EntryOverclockSettings(MenuOptionList parent) {
        super(parent, "Overclocking Settings", Color.white, 12);
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new NotificationActionPanel(
            "Overclocking not supported",
            new DefaultActionElement()
        ));
    }
}
