package menu;

import java.awt.Color;
import java.awt.Point;

import ui.UIText;

public abstract class MenuEntry extends UIText {
    static final float LERP_CONSTANT = 0.2f;

    boolean isSelected = false;
    Point origOffset;
    Point target;
    float curLerpX;

    public MenuEntry(MenuOptionList parent, String text, Color color, int fontSize) {
        super(parent, new Point(), text, color, fontSize);
        parent.addMenuOption(this);
        origOffset = new Point(this.location);
    }

    protected final void tick() {
        this.curLerpX = lerpX(this.location, this.target, LERP_CONSTANT);
        this.location = new Point(Math.round(this.curLerpX), this.location.y);

        if (Math.abs(this.curLerpX - this.target.x) < 2)
            this.location.x = this.target.x;

        menuTick();
    }

    protected void menuTick() {}

    public final void register(Point offset) {
        this.origOffset = new Point(this.location);
        this.location = offset;
        this.target = new Point(offset);
    }

    public void select() {
        this.target = new Point(origOffset.x + 12, origOffset.y);
        this.isSelected = true;
    }

    public void unselect() {
        this.target = origOffset;
        this.isSelected = false;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    static float lerpX(Point start, Point end, float t) {
        float a = start.x;
        float b = end.x;
        return a + t * (b - a);
    }

    public abstract void execute();
}
