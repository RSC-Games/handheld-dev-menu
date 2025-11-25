package menu.settings.network.network_list.edit_network;

import java.awt.Color;

import backend.NetworkBackend;
import backend.network.SavedNetwork;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.ActionPanel;
import menu.action_panel.ActionableElement;
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
        PanelManager.getPanelManager().pushPanel(new ActionPanel("Network forgotten!", new ActionableElement() {
            protected void trigger() {
                for (int i = 0; i < 3; i++)
                    PanelManager.getPanelManager().popPanel();
            }
        }));
    }
}
