package menu.settings.perf_power_mgmt;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.DefaultActionElement;
import system.PanelManager;

class EntryFanSettings extends MenuEntry {
    // TODO: is a submenu; needs a submenu
    public EntryFanSettings(MenuOptionList parent) {
        super(parent, "Fan Settings", Color.white, 12);
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new NotificationActionPanel(
            "Changing fan settings not supported",
            new DefaultActionElement()
        ));
    }
}
