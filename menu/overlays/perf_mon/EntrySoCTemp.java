package menu.overlays.perf_mon;

import java.awt.Color;
import java.awt.Point;

import backend.PerformanceMonitor;
import ui.UIElement;
import ui.UIText;

public class EntrySoCTemp extends UIText {

    public EntrySoCTemp(UIElement parent, Point location) {
        super(parent, location, "85C", Color.red, 12);
    }
    
    @Override
    public void tick() {
        // TODO: Color changing temp tracking
        setText(PerformanceMonitor.getSoCTemperature() + "C");
    }
}
