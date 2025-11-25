package menu.main;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.power.PowerMenu;
import system.PanelManager;

class EntryPower extends MenuEntry {

    public EntryPower(MenuOptionList parent) {
        super(parent, "Power Menu", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager manager = PanelManager.getPanelManager();
        manager.pushPanel(new PowerMenu());
    }
}
