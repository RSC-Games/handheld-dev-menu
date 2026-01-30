package menu.settings.perf_power_mgmt;

import java.awt.Color;
import java.awt.Point;

import menu.MenuEntry;
import menu.MenuOptionList;
import system.PerformanceOverlayThread;
import ui.UIKeyValueText;

public class EntryPerformanceOverlay extends MenuEntry {
    PerformanceOverlayThread overlayThread = PerformanceOverlayThread.getOverlayThread();

    boolean isEnabled = false;
    UIKeyValueText uiText;

    // Performance overlay can now be toggled.s
    public EntryPerformanceOverlay(MenuOptionList parent) {
        super(parent, "", Color.white, 12);
        this.uiText = new UIKeyValueText(this, new Point(), Color.white);
        this.uiText.setKeyText("Perfmon Overlay Enabled");
    }

    @Override
    public void menuTick() {
        this.isEnabled = overlayThread.getIsRunning();
        this.uiText.setValueText(isEnabled ? "Yes" : "No");
        this.uiText.setValueColor(isEnabled ? Color.green : Color.red);
    }

    @Override
    public void execute() {
        if (isEnabled)
            overlayThread.stop();
        else
            overlayThread.start();

        isEnabled = !isEnabled;
    }
    
}
