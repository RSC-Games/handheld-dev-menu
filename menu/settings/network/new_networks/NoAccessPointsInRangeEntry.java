package menu.settings.network.new_networks;

import java.awt.Color;

import backend.network.AccessPoint;
import menu.MenuEntry;
import menu.MenuOptionList;

class NoAccessPointsInRangeEntry extends MenuEntry {
    AccessPoint ap;

    public NoAccessPointsInRangeEntry(MenuOptionList parent) {
        super(parent, "No access points in range...", Color.white, 12);
    }

    @Override
    public void execute() {

    }
}
