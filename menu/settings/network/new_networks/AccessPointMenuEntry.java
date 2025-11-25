package menu.settings.network.new_networks;

import java.awt.Color;

import backend.network.AccessPoint;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.settings.network.new_networks.connect_network.ConnectionMenu;
import system.PanelManager;

class AccessPointMenuEntry extends MenuEntry {
    AccessPoint ap;

    public AccessPointMenuEntry(MenuOptionList parent, AccessPoint ap) {
        super(parent, "", Color.white, 12);
        this.text = String.format("%s [%s]: %d MHz, %d dBm, flags: %s", ap.ssid, ap.bssid, ap.frequency,
                                  ap.rssi, ap.getPlaintextFlags());
        this.ap = ap;
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new ConnectionMenu(ap));
    }
    
}
