package menu.settings;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.power.PowerMenu;
import system.PanelManager;

class EntryAccountManagement extends MenuEntry {

    public EntryAccountManagement(MenuOptionList parent) {
        super(parent, "Account Management", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager manager = PanelManager.getPanelManager();
        manager.pushPanel(new PowerMenu());
    }
    // TODO: Add notifications to account settings
}
