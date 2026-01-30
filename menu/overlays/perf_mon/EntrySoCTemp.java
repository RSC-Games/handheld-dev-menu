package menu.overlays.perf_mon;

import java.awt.Color;
import java.awt.Point;

import backend.PerformanceMonitor;
import ui.UIElement;
import ui.UIText;

class EntrySoCTemp extends UIText {

    public EntrySoCTemp(UIElement parent, Point location) {
        super(parent, location, "85C", Color.red, 12);
    }
    
    @Override
    public void tick() {
        int temp = PerformanceMonitor.getSoCTemperature();

        if (temp <= 40)
            setColor(Color.green);
        else if (temp <= 55)
            setColor(Color.yellow);
        else if (temp <= 65)
            setColor(Color.orange);
        else
            setColor(Color.red);

        setText(temp + "C");
    }
}
