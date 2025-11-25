package menu.settings.network.network_list.edit_network;

import java.awt.Color;

import backend.NetworkBackend;
import backend.network.SavedNetwork;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.ActionPanel;
import menu.action_panel.ActionableElement;
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
        PanelManager.getPanelManager().pushPanel(new ActionPanel("Connecting! Press OKAY to return to the menu", 
            new ActionableElement() {
                protected void trigger() {
                    for (int i = 0; i < 3; i++)
                        PanelManager.getPanelManager().popPanel();
                }
            }
        ));
    }
}
