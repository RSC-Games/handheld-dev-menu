package menu.overlays.perf_mon;

import java.awt.Color;
import java.awt.Point;

import backend.PerformanceMonitor;
import ui.UIElement;
import ui.UIText;

class EntrySoCVoltage extends UIText {

    public EntrySoCVoltage(UIElement parent, Point location) {
        super(parent, location, "0.95V", new Color(192, 192, 192), 12);
    }
    
    @Override
    public void tick() {
        setText(String.format("%.2fV", PerformanceMonitor.getSoCVcore()));
    }
}
