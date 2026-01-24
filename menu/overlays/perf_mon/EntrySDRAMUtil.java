package menu.overlays.perf_mon;

import java.awt.Color;
import java.awt.Point;

import backend.PerformanceMonitor;
import ui.UIElement;
import ui.UIKeyValueText;

public class EntrySDRAMUtil extends UIKeyValueText {

    public EntrySDRAMUtil(UIElement parent, Point location) {
        super(parent, location, Color.white);
        setKeyText("RAM");
        setKeyColor(Color.yellow);
    }
    
    @Override
    public void tick() {
        float totalRAM = PerformanceMonitor.getSDRAMTotalMB() / 1000f;
        float usedRAM = totalRAM - PerformanceMonitor.getSDRAMAvailableMB() / 1000f;
        setValueText(String.format("%.1f/%.1f GB", usedRAM, totalRAM));
    }
}
