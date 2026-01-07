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
        super(parent, new Point(0, 50), text, color, fontSize);
        this.registerWithMenu(parent);
    }

    /**
     * Intended for lazy binding a menu entry to a menu. Should not be called more than once
     * or there's a high risk of having multiple menus share the same button.
     * 
     * @param parent The parent to bind to.
     */
    public void registerWithMenu(MenuOptionList parent) {
        if (parent != null) {
            if (!parent.hasChild(this))
                parent.addChild(this);

            parent.addMenuOption(this);
        }
    }

    protected final void tick() {
        this.curLerpX = lerpX(this.location, this.target, LERP_CONSTANT);
        this.location = new Point(Math.round(this.curLerpX), this.location.y);

        if (Math.abs(this.curLerpX - this.target.x) < 2)
            this.location.x = this.target.x;

        menuTick();
    }

    protected void menuTick() {}

    /**
     * Prepare the UI element for being used in a linked menu system.
     * 
     * @param offset The relative vertical offset from the menu starting point.
     */
    final void register(Point offset) {
        this.location = offset;
        this.target = new Point(offset);
        this.origOffset = new Point(this.location);
    }

    /**
     * Event handler for when this option comes under focus.
     */
    void select() {
        this.target = new Point(origOffset.x + 12, origOffset.y);
        this.isSelected = true;
    }

    /**
     * Event handler for when this option is no longer under focus.
     */
    void unselect() {
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

    /**
     * Event triggered when this option is pressed/triggered.
     */
    public abstract void execute();
}
