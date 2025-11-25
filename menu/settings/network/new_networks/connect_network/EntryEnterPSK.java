package menu.settings.network.new_networks.connect_network;

import java.awt.Color;

import backend.network.AccessPoint;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.ActionPanel;
import menu.action_panel.ActionableElement;
import system.PanelManager;

class EntryEnterPSK extends MenuEntry {
    AccessPoint ap;

    public EntryEnterPSK(MenuOptionList parent, AccessPoint ap) {
        super(parent, "Enter PSK", Color.WHITE, 12);
        this.ap = ap;
    }

    // TODO: PSK support
    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new ActionPanel("ERROR: PSK entry not implemented!", new ActionableElement() {
            protected void trigger() {
                for (int i = 0; i < 3; i++)
                    PanelManager.getPanelManager().popPanel();
            }
        }));
    }
}
