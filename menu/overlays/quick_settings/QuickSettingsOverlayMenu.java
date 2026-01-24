package menu.overlays.quick_settings;

import java.awt.Color;
import java.awt.Point;

import menu.BackButton;
import menu.MenuOptionList;
import menu.PanelPathPrint;
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
        new BackButton(root);
        new UIText(root, new Point(3, 0), "OVERLAY - DEV MENU " + Version.VERSION, Color.WHITE, 12);
        new PanelPathPrint(root, new Point(10, 40));

        ///////////////////////////// MENU OPTIONS /////////////////////////////////
        UIElement bodyArea = new UIClip(root, new Point(0, 60), new Point(800, 480 - 15));
        MenuOptionList menu = new MenuOptionList(bodyArea, new Point(30, 80));
        new UIText(bodyArea, new Point(20, 80), ">", Color.WHITE, 12);
        //new EntryDisplayDimSlider(menu);    // Display Dim Timeout: < 1m >
        //new EntrySleepModeSlider(menu);     // Sleep Mode Timeout: < 10m >
        // TODO: Enable Overclocking menu entry
        //new EntryOverclockSettings(menu);   // Overclocking Settings 
            // (note: add include config/overclock-pi4.txt) (can set core_freq to 550 MHz for free?)
        //new EntryPerformanceOverlay(menu);  // Performance Overlay Enabled: Yes
        //new EntryFanSettings(menu);         // Fan Settings (Change fan curve/temps) (include config/fan-pi4.txt)
        //new EntryEnableThrottling(menu);    // Throttle when Low (< 5%) Battery: Yes
    }
}
