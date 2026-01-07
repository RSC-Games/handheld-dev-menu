package menu.settings.network.network_list.edit_network;

import java.awt.Color;

import backend.NetworkBackend;
import backend.network.SavedNetwork;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.DefaultActionElement;
import system.PanelManager;

class EntryConnectNetwork extends MenuEntry {
    SavedNetwork network;

    public EntryConnectNetwork(MenuOptionList parent, SavedNetwork network) {
        super(parent, "Connect", Color.WHITE, 12);
        this.network = network;
    }

    @Override
    public void execute() {
        NetworkBackend.reconnect(network);
        PanelManager.getPanelManager().pushPanel(new NotificationActionPanel("Connecting! Press OKAY to return to the menu",
            new DefaultActionElement(3)
        ));
    }
}
