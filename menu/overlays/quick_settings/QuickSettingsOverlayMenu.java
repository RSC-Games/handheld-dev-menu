package menu.overlays.quick_settings;

import java.awt.Color;
import java.awt.Point;

import menu.MenuOptionList;
import menu.settings.display_audio.EntryBrightness;
import menu.settings.display_audio.EntryOutputVolume;
import menu.settings.perf_power_mgmt.EntryPerformanceOverlay;
import ui.UIBackdrop;
import ui.UIBase;
import ui.UIClip;
import ui.UIElement;
import ui.UIPanel;
import ui.UIText;
import util.Version;

public class QuickSettingsOverlayMenu extends UIPanel {
    public QuickSettingsOverlayMenu() {
        super("quick_settings");

        root = new UIBase();
        new UIBackdrop(root);
        new UIText(root, new Point(3, 0), "OVERLAY - DEV MENU " + Version.VERSION, Color.WHITE, 12);
        new UIText(root, new Point(10, 40), "@: overlay_menu", Color.white, 12);

        ///////////////////////////// MENU OPTIONS /////////////////////////////////
        UIElement bodyArea = new UIClip(root, new Point(0, 60), new Point(800, 480 - 15));

        // Show the system time, wifi status, and IP address.
        new MiniConnectionInfo(root, new Point(10, 60));
        new MiniTimeInfo(root, new Point(10, 75));

        MenuOptionList menu = new MenuOptionList(bodyArea, new Point(30, 80));
        new UIText(bodyArea, new Point(20, 80), ">", Color.WHITE, 12);
        new CloseUIOnBack(root);
        // Side window needs this:
        // System time - Wifi Status - IP address
        //
        // Brightness slider
        // Volume slider
        // Enable Performance Overlay
        // ... More later if necessary.
        new EntryBrightness(menu);
        new EntryOutputVolume(menu);
        new EntryPerformanceOverlay(menu);
    }
}
