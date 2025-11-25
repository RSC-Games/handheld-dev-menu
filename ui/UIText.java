package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

public class UIText extends UIElement {
    protected String text;
    protected Color color;
    protected Font font;

    public UIText(Point loc, String text, Color color, int size) {
        this(null, loc, text, color, size);
    }

    public UIText(UIElement parent, Point loc, String text, Color color, int size) {
        super(parent, loc);
        this.text = text;
        this.color = color;
        this.font = new Font(Font.MONOSPACED, Font.PLAIN, size);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Font getFont() {
        return this.font;
    }

    @Override
    protected void draw(Point drawLoc, Graphics g) {
        if (text == null)
            return;

        int offset = this.font.getBaselineFor(this.text.length() > 0 ? this.text.charAt(0) : '_');
        int size = this.font.getSize();
        g.setFont(this.font);
        g.setColor(this.color);
        g.drawString(text, drawLoc.x, drawLoc.y + (size - offset));
    }

    @Override
    protected void tick() {}
}
