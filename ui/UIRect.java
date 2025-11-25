package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class UIRect extends UIElement {
    Point wh;
    Color color;

    public UIRect(UIElement parent, Point loc, Point wh, Color color) {
        super(parent, loc);
        this.wh = wh;
        this.color = color;
    }

    @Override
    protected void tick() {}

    @Override
    protected void draw(Point drawLoc, Graphics g) {
        g.setColor(color);
        g.fillRect(location.x, location.y, wh.x, wh.y);
    }
}
