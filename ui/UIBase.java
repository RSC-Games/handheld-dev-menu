package ui;

import java.awt.Graphics;
import java.awt.Point;

public class UIBase extends UIElement {
    public UIBase() {
        this(null, new Point());
    }

    public UIBase(UIElement parent, Point location) {
        super(parent, location);
    }

    @Override
    protected void tick() {}

    @Override
    protected void draw(Point drawLoc, Graphics g) {}
}
