package ui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

public class UIClip extends UIElement {
    Shape clipBounds;
    Point wh;

    public UIClip(UIElement parent, Point loc, Point wh) {
        super(parent, loc);
        this.wh = wh;
    }

    @Override
    protected void tick() {}

    @Override
    protected void draw(Point drawLoc, Graphics g) {
        // This object isn't rendered, but it affects all child objects and clips their rendering area.
        clipBounds = g.getClip();
        Rectangle newClip = new Rectangle(drawLoc.x, drawLoc.y, wh.x, wh.y);
        g.setClip(newClip);
    }
    
    @Override
    protected void drawAfter(Point drawLoc, Graphics g) {
        // Restore old clip boundaries.
        g.setClip(clipBounds);
    }
}
