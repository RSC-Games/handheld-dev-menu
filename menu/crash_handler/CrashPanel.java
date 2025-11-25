package menu.crash_handler;

import java.awt.Color;
import java.awt.Point;

import menu.BackButton;
import menu.PanelPathPrint;
import ui.UIBackdrop;
import ui.UIBase;
import ui.UIPanel;
import ui.UIText;
import util.Version;

public class CrashPanel extends UIPanel {
    /**
     * Show an alert message on screen with a configurable action.
     * 
     * @param message Alert message
     * @param actionElement Trigger action when okay is selected
     */
    public CrashPanel(String[] exceptionBacktrace) {
        super("crash_handler");

        root = new UIBase();
        new UIBackdrop(root);
        new BackButton(root);
        new UIText(root, new Point(3, 0), "Development Menu " + Version.VERSION, Color.WHITE, 12);
        new UIText(root, new Point(665, 0), "(c) 2025 RSC Games", Color.white, 12);
        new PanelPathPrint(root, new Point(10, 40));

        new UIText(root, new Point(15, 80), "@@@@@@ ENCOUNTERED FATAL ERROR @@@@@@ (Press A to exit)", Color.red, 12);
        new BacktracePrinter(root, new Point(25, 100), exceptionBacktrace);
        new ExitButton(root);
    }
}
