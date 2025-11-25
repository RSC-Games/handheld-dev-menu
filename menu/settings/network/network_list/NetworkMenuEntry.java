package menu.settings.network.network_list;

import java.awt.Color;

import backend.network.SavedNetwork;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.settings.network.network_list.edit_network.EditNetworkMenu;
import system.PanelManager;

class NetworkMenuEntry extends MenuEntry {
    SavedNetwork network;

    public NetworkMenuEntry(MenuOptionList parent, SavedNetwork network) {
        super(parent, "", Color.white, 12);
        this.text = String.format("%d: %s (%s) %s", network.networkID, network.ssid, network.bssid,
                                  "[" + network.state.toString() + "]");
        this.network = network;
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new EditNetworkMenu(network));
    }
    
}
