package menu.settings.network.network_list.edit_network;

import java.awt.Color;

import backend.NetworkBackend;
import backend.network.SavedNetwork;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.ActionPanel;
import menu.action_panel.ActionableElement;
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

        PanelManager.getPanelManager().pushPanel(new ActionPanel("Network disabled!", new ActionableElement() {
            protected void trigger() {
                for (int i = 0; i < 3; i++)
                    PanelManager.getPanelManager().popPanel();
            }
        }));
    }
}
