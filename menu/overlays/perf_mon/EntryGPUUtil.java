package menu.overlays.perf_mon;

import java.awt.Color;
import java.awt.Point;

import backend.PerformanceMonitor;
import ui.UIElement;
import ui.UIKeyValueText;

public class EntryGPUUtil extends UIKeyValueText {

    public EntryGPUUtil(UIElement parent, Point location) {
        super(parent, location, Color.white);
        setKeyText("V3D");
        setKeyColor(Color.magenta);
    }
    
    @Override
    public void tick() {
        setValueText(PerformanceMonitor.getGPUUtilization() + "%");
    }
}
