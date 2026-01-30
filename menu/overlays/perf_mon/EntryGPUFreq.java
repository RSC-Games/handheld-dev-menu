package menu.overlays.perf_mon;

import java.awt.Color;
import java.awt.Point;

import backend.PerformanceMonitor;
import ui.UIElement;
import ui.UIText;

class EntryGPUFreq extends UIText {

    public EntryGPUFreq(UIElement parent, Point location) {
        super(parent, location, "0.0 kHz", Color.white, 12);
    }
    
    @Override
    public void tick() {
        setText(PerformanceMonitor.getGPUFrequencyMHz() + " MHz");
    }
}
