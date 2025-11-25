package menu.settings;

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

public class SettingsMenu extends UIPanel {
    public SettingsMenu() {
        super("settings_menu");

        root = new UIBase();
        new UIBackdrop(root);
        new BackButton(root);
        new UIText(root, new Point(3, 0), "Development Menu " + Version.VERSION, Color.WHITE, 12);
        new UIText(root, new Point(665, 0), "(c) 2025 RSC Games", Color.white, 12);
        new PanelPathPrint(root, new Point(10, 40));

        ///////////////////////////// MENU OPTIONS /////////////////////////////////
        UIElement bodyArea = new UIClip(root, new Point(0, 60), new Point(800, 480 - 15));
        MenuOptionList menu = new MenuOptionList(bodyArea, new Point(30, 80));
        new UIText(bodyArea, new Point(20, 80), ">", Color.WHITE, 12);
        new EntryNetwork(menu);             // >   Network Settings (done)
        new EntryBluetooth(menu);           //   Bluetooth Settings (need to impl)
        new EntryDisplayAndAudio(menu);     //   Display and Audio (mostly done)
        new EntryTitleManagement(menu);     //   Title Management (need to impl)
        new EntryAccountManagement(menu);   //   Account Management (need to impl)
        new EntryPowerManagement(menu);     //   Power Management (need to impl)
        new EntryLightingEffects(menu);     //   RGB and System Lighting (need to impl)
        new EntryUpdate(menu);              //   Install Updates (need to impl)
    }
}
