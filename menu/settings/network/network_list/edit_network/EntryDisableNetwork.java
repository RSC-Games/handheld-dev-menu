package menu.settings.network.network_list.edit_network;

import java.awt.Color;

import backend.NetworkBackend;
import backend.network.SavedNetwork;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.DefaultActionElement;
import system.PanelManager;

class EntryDisableNetwork extends MenuEntry {
    SavedNetwork network;

    public EntryDisableNetwork(MenuOptionList parent, SavedNetwork network) {
        super(parent, "Disable Network", Color.WHITE, 12);
        this.network = network;
    }

    @Override
    public void execute() {
        NetworkBackend.disableNetwork(network);

        PanelManager.getPanelManager().pushPanel(
            new NotificationActionPanel("Network disabled!",
            new DefaultActionElement(3)
        ));
    }
}
