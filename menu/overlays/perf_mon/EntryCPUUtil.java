package menu.overlays.perf_mon;

import java.awt.Color;
import java.awt.Point;

import backend.PerformanceMonitor;
import ui.UIElement;
import ui.UIKeyValueText;

public class EntryCPUUtil extends UIKeyValueText {

    public EntryCPUUtil(UIElement parent, Point location) {
        super(parent, location, Color.white);
        setKeyText("ARM");
        setKeyColor(Color.cyan);
    }
    
    @Override
    public void tick() {
        setValueText(PerformanceMonitor.getCPUUtilization() + "%");
    }
}
