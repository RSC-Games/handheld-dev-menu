package menu.settings.network.network_list.edit_network;

import java.awt.Color;

import backend.NetworkBackend;
import backend.network.SavedNetwork;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.DefaultActionElement;
import system.PanelManager;

class EntryRemoveNetwork extends MenuEntry {
    SavedNetwork network;

    public EntryRemoveNetwork(MenuOptionList parent, SavedNetwork network) {
        super(parent, "Remove Network", Color.WHITE, 12);
        this.network = network;
    }

    @Override
    public void execute() {
        NetworkBackend.forget(network);
        PanelManager.getPanelManager().pushPanel(
            new NotificationActionPanel("Network forgotten!", 
            new DefaultActionElement(3)
        ));
    }
}
