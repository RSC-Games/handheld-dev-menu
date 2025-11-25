package menu.settings;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.settings.network.NetworkMenu;
import system.PanelManager;

class EntryNetwork extends MenuEntry {

    public EntryNetwork(MenuOptionList parent) {
        super(parent, "Network Settings", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new NetworkMenu());
    }
}
