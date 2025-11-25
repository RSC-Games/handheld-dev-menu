package menu.main;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.settings.SettingsMenu;
import system.PanelManager;

class EntrySettings extends MenuEntry {

    public EntrySettings(MenuOptionList parent) {
        super(parent, "System Settings", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager manager = PanelManager.getPanelManager();
        manager.pushPanel(new SettingsMenu());
    }
}
