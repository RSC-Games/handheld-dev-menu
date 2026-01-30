package menu.action_panel;

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

public class ConfirmActionPanel extends UIPanel {
    /**
     * Show a confirm dialog on screen with two custom actions
     * 
     * @param message Alert message
     * @param yesActionElement Trigger action when yes is selected
     * @param noActionElement Trigger action when no is selected
     */
    public ConfirmActionPanel(String message, ActionableElement yesActionElement, ActionableElement noActionElement) {
        super("confirm");

        root = new UIBase();
        new UIBackdrop(root);
        new BackButton(root);
        new UIText(root, new Point(3, 0), "Development Menu " + Version.VERSION, Color.WHITE, 12);
        new UIText(root, new Point(665, 0), "(c) 2025 RSC Games", Color.white, 12);
        new PanelPathPrint(root, new Point(10, 40));

        ///////////////////////////// MENU OPTIONS /////////////////////////////////
        UIElement bodyArea = new UIClip(root, new Point(0, 60), new Point(800, 480 - 15));
        new UIText(bodyArea, new Point(20, 80), ">>> " + message, Color.WHITE, 12);
        MenuOptionList optionList = new MenuOptionList(bodyArea, new Point(30, 95));

        // TODO: Fix (looks horrible by default)
        // Force consistency.
        yesActionElement.setText("Yes");
        yesActionElement.setColor(Color.green);
        noActionElement.setText("No");
        noActionElement.setColor(Color.red);

        noActionElement.registerWithMenu(optionList);
        yesActionElement.registerWithMenu(optionList);
    }
}
