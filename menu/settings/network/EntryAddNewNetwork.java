package menu.settings.network;

import java.awt.Color;
import java.awt.Point;

import backend.NetworkBackend;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.DefaultActionElement;
import menu.settings.network.new_networks.AddNetworksMenu;
import system.PanelManager;
import ui.UIText;

class EntryAddNewNetwork extends MenuEntry {
    UIText onOff;

    public EntryAddNewNetwork(MenuOptionList parent) {
        super(parent, "Add New Network", Color.WHITE, 12);
        this.onOff = new UIText(this, new Point(100, 0), "unset", Color.black, 12);
    }

    @Override
    public void execute() {
        if (!NetworkBackend.wlanEnabled()) {
            PanelManager.getPanelManager().pushPanel(new NotificationActionPanel("Please enable WLAN first!",
                new DefaultActionElement()
            ));
        }
        else
            PanelManager.getPanelManager().pushPanel(new AddNetworksMenu());
    }
}
