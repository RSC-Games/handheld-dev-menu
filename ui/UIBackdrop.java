package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class UIBackdrop extends UIElement {
    public UIBackdrop(UIElement parent) {
        super(parent, new Point());
    }

    @Override
    protected void tick() {}

    @Override
    protected void draw(Point drawLoc, Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 480);
    }
    
}
