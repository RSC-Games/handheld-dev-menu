package menu.settings;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.settings.bluetooth.BluetoothMenu;
import system.PanelManager;

class EntryBluetooth extends MenuEntry {

    public EntryBluetooth(MenuOptionList parent) {
        super(parent, "Bluetooth Settings", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new BluetoothMenu());
    }
}
