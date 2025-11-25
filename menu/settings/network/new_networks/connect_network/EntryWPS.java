package menu.settings.network.new_networks.connect_network;

import java.awt.Color;

import backend.network.AccessPoint;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.ActionPanel;
import menu.action_panel.ActionableElement;
import system.PanelManager;

class EntryWPS extends MenuEntry {
    AccessPoint ap;

    public EntryWPS(MenuOptionList parent, AccessPoint ap) {
        super(parent, "Use WPS Pushbutton", Color.WHITE, 12);
        this.ap = ap;
    }

    // TODO: WPS support.
    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new ActionPanel("ERROR: WPS not yet supported", 
            new ActionableElement() {
                protected void trigger() {
                    for (int i = 0; i < 3; i++)
                        PanelManager.getPanelManager().popPanel();
                }
            }
        ));
    }
}
