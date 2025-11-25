package menu.settings.display_audio;

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

public class DisplayAndAudioMenu extends UIPanel {
    public DisplayAndAudioMenu() {
        super("display_and_audio");

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
        new EntryBrightness(menu);    // >   Brightness <100>
        new EntryAudioEndpoint(menu);   //   Audio Output Device (new menu)
        new EntryOutputVolume(menu);  //   Output Volume <100>
        new EntryMuteOutput(menu);      //   Mute Output: <yes/no>
    }
}
