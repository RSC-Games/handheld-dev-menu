package menu.settings.network.new_networks.connect_network;

import java.awt.Color;
import java.awt.Point;

import backend.NetworkBackend;
import backend.network.AccessPoint;
import menu.BackButton;
import menu.MenuOptionList;
import menu.PanelPathPrint;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.ActionableElement;
import menu.action_panel.DefaultActionElement;
import system.PanelManager;
import ui.UIBackdrop;
import ui.UIBase;
import ui.UIClip;
import ui.UIElement;
import ui.UIPanel;
import ui.UIText;
import util.Version;

public class ConnectionMenu extends UIPanel {
    public ConnectionMenu(AccessPoint ap) {
        super("connect_network");
        String networkRepr = String.format(">>> network: %s [%s]: %d MHz, %d dBm, flags: %s", ap.ssid, ap.bssid, 
                                           ap.frequency, ap.rssi, ap.getPlaintextFlags());

        root = new UIBase();
        new UIBackdrop(root);
        new BackButton(root);
        new UIText(root, new Point(3, 0), "Development Menu " + Version.VERSION, Color.WHITE, 12);
        new UIText(root, new Point(665, 0), "(c) 2025 RSC Games", Color.white, 12);
        new PanelPathPrint(root, new Point(10, 40));

        ///////////////////////////// MENU OPTIONS /////////////////////////////////
        UIElement bodyArea = new UIClip(root, new Point(0, 60), new Point(800, 480 - 15));
        MenuOptionList menu = new MenuOptionList(bodyArea, new Point(30, 80));
        new UIText(root, new Point(30, 65), networkRepr, Color.white, 12);
        new UIText(bodyArea, new Point(20, 80), ">", Color.WHITE, 12);

        // Depending on the network type we may or may not need a password.
        // WEP unsupported.
        if (ap.isWEP()) {
            PanelManager.getPanelManager().pushPanel(new NotificationActionPanel(
                "WEP network not supported!", 
                new DefaultActionElement(3)
            ));
        }

        // Needs password auth (or WPS_PBC if available)
        else if (ap.needsAuthentication()) {
            new EntryEnterPSK(menu, ap);

            if (ap.WPSAvailable())
                new EntryWPS(menu, ap);
        }

        // Open network (just automatically connect)
        else {
            PanelManager.getPanelManager().pushPanel(new NotificationActionPanel("Connecting to open network", 
                new ActionableElement() {
                    protected void trigger() {
                        NetworkBackend.associate(ap, "");

                        for (int i = 0; i < 3; i++)
                            PanelManager.getPanelManager().popPanel();
                    }
                }
            ));
        }
    }
}
