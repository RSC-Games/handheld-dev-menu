package menu.settings;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.DefaultActionElement;
import menu.settings.network.NetworkMenu;
import system.Config;
import system.PanelManager;

class EntryNetwork extends MenuEntry {

    public EntryNetwork(MenuOptionList parent) {
        super(parent, "Network Settings", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        if (Config.ENABLE_NETWORK_BACKEND)
            PanelManager.getPanelManager().pushPanel(new NetworkMenu());

        // Network support component has been disabled in this build.
        else {
            PanelManager.getPanelManager().pushPanel(
                new NotificationActionPanel("Network support has been disabled in this build!", new DefaultActionElement())
            );
        }
    }
}
