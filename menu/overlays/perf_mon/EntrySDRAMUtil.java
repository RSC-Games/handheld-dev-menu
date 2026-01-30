package menu.overlays.perf_mon;

import java.awt.Color;
import java.awt.Point;

import backend.PerformanceMonitor;
import ui.UIElement;
import ui.UIKeyValueText;

class EntrySDRAMUtil extends UIKeyValueText {
    private float totalRAM;

    public EntrySDRAMUtil(UIElement parent, Point location) {
        super(parent, location, Color.white);
        setKeyText("RAM");
        setKeyColor(Color.yellow);

        totalRAM = PerformanceMonitor.getSDRAMTotalMB() / 1024f;
    }
    
    @Override
    public void tick() {
        float usedRAM = totalRAM - PerformanceMonitor.getSDRAMAvailableMB() / 1024f;
        setValueText(String.format("%.1f/%.1f GB", usedRAM, totalRAM));
    }
}
