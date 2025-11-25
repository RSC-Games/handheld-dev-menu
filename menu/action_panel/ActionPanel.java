package menu.action_panel;

import java.awt.Color;
import java.awt.Point;

import menu.BackButton;
import menu.PanelPathPrint;
import ui.UIBackdrop;
import ui.UIBase;
import ui.UIClip;
import ui.UIElement;
import ui.UIPanel;
import ui.UIText;
import util.Version;

public class ActionPanel extends UIPanel {
    /**
     * Show an alert message on screen with a configurable action.
     * 
     * @param message Alert message
     * @param actionElement Trigger action when okay is selected
     */
    public ActionPanel(String message, ActionableElement actionElement) {
        super("info");

        root = new UIBase();
        new UIBackdrop(root);
        new BackButton(root);
        new UIText(root, new Point(3, 0), "Development Menu " + Version.VERSION, Color.WHITE, 12);
        new UIText(root, new Point(665, 0), "(c) 2025 RSC Games", Color.white, 12);
        new PanelPathPrint(root, new Point(10, 40));

        ///////////////////////////// MENU OPTIONS /////////////////////////////////
        UIElement bodyArea = new UIClip(root, new Point(0, 60), new Point(800, 480 - 15));
        new UIText(bodyArea, new Point(20, 80), ">>> " + message, Color.WHITE, 12);
        new UIText(bodyArea, new Point(30, 92), " < OKAY >", Color.white, 12);
        bodyArea.addChild(actionElement);
    }
}
