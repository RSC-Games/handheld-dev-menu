package menu.settings.network;

import java.awt.Color;

import backend.NetworkBackend;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.ActionPanel;
import menu.action_panel.ActionableElement;
import menu.settings.network.network_list.NetworksListMenu;
import system.PanelManager;

class EntryListNetworks extends MenuEntry {
    public EntryListNetworks(MenuOptionList parent) {
        super(parent, "Registered Networks", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        if (!NetworkBackend.wlanEnabled()) {
            PanelManager.getPanelManager().pushPanel(new ActionPanel("Please enable WLAN first!",
                new ActionableElement() {
                    protected void trigger() {
                        PanelManager.getPanelManager().popPanel();
                    }
                }
            ));
        }
        else
            PanelManager.getPanelManager().pushPanel(new NetworksListMenu());
    }
}
