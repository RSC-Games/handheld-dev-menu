package menu.overlays.perf_mon;

import java.awt.Point;

import ui.UIBackdrop;
import ui.UIBase;
import ui.UIPanel;

public class PerfMonOverlay extends UIPanel {
    public PerfMonOverlay() {
        super("perf_mon");

        root = new UIBase();
        new UIBackdrop(root);

        // TODO: FPS counter?
        new EntryCPUUtil(root, new Point(3, -2));
        // TODO: Per-core vertical bar?
        new EntryCPUFreq(root, new Point(75, -2));
        new EntryGPUUtil(root, new Point(135, -2));
        new EntryGPUFreq(root, new Point(205, -2));
        new EntrySoCVoltage(root, new Point(270, -2));
        new EntrySoCTemp(root, new Point(315, -2));
        new EntrySDRAMUtil(root, new Point(347, -2));
        //new UIText(root, new Point(3, -2), "OVERLAY - DEV MENU " + Version.VERSION, Color.WHITE, 12);
    }
}
