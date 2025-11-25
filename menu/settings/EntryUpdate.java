package menu.settings;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.settings.install_updates.UpdatesMenu;
import system.PanelManager;

class EntryUpdate extends MenuEntry {

    public EntryUpdate(MenuOptionList parent) {
        super(parent, "Install Updates", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new UpdatesMenu());
    }
}
