package menu.overlays.perf_mon;

import java.awt.Color;
import java.awt.Point;

import backend.PerformanceMonitor;
import ui.UIElement;
import ui.UIText;

class EntryCPUFreq extends UIText {

    public EntryCPUFreq(UIElement parent, Point location) {
        super(parent, location, "0.0 kHz", Color.white, 12);
    }
    
    @Override
    public void tick() {
        float freqGHz = PerformanceMonitor.getCPUFrequencyMHz() / 1000f;
        setText(String.format("%.1f GHz", freqGHz));
    }
}
