package menu.settings.network;

import java.awt.Color;

import backend.NetworkBackend;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.DefaultActionElement;
import menu.settings.network.network_list.NetworksListMenu;
import system.PanelManager;

class EntryListNetworks extends MenuEntry {
    public EntryListNetworks(MenuOptionList parent) {
        super(parent, "Registered Networks", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        if (!NetworkBackend.wlanEnabled()) {
            PanelManager.getPanelManager().pushPanel(new NotificationActionPanel("Please enable WLAN first!",
                new DefaultActionElement()
            ));
        }
        else
            PanelManager.getPanelManager().pushPanel(new NetworksListMenu());
    }
}
