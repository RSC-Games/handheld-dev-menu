package ui;

import java.awt.Graphics;
import java.awt.Point;

public abstract class UIPanel {
    protected String name;
    protected UIElement root;

    public UIPanel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public final void update() {
        root.tickAll();
    }

    public final void drawAll(Graphics g) {
        root.drawAll(new Point(), g);
    }
}