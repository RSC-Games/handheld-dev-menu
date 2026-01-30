package menu.overlays.quick_settings;

import java.awt.Color;
import java.awt.Point;
import java.time.LocalTime;

import ui.UIElement;
import ui.UIText;

class MiniTimeInfo extends UIText {
    private static final int REFRESH_FRAMES = 60;
    int counter = 0;

    public MiniTimeInfo(UIElement parent, Point loc) {
        super(parent, loc, "time: 00:00", Color.white, 12);
    }
    
    @Override
    public void tick() {
        if (counter-- >= 0)
            return;

        LocalTime time = LocalTime.now();
        this.setText(String.format("time: %d:%02d", time.getHour(), time.getMinute()));
        counter = REFRESH_FRAMES;
    }
}
