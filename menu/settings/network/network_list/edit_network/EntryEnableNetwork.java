package menu.settings.network.network_list.edit_network;

import java.awt.Color;

import backend.NetworkBackend;
import backend.network.SavedNetwork;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.ActionableElement;
import system.PanelManager;

class EntryEnableNetwork extends MenuEntry {
    SavedNetwork network;

    public EntryEnableNetwork(MenuOptionList parent, SavedNetwork network) {
        super(parent, "Enable Network", Color.WHITE, 12);
        this.network = network;
    }

    @Override
    public void execute() {
        NetworkBackend.enableNetwork(network);
        PanelManager.getPanelManager().pushPanel(new NotificationActionPanel("Network enabled!", new ActionableElement() {
            protected void trigger() {
                for (int i = 0; i < 3; i++)
                    PanelManager.getPanelManager().popPanel();
            }
        }));
    }
}
