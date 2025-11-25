package ui;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public abstract class UIElement {
    protected Point location;
    protected ArrayList<UIElement> children = new ArrayList<>();

    /**
     * Create a new generic UIElement. Position will be the local origin.
     * @param loc
     */
    public UIElement() {
        this(null, new Point());
    }

    /**
     * Create a new generic UIElement. Position is relative to any parent element.
     * @param loc
     */
    public UIElement(Point loc) {
        this(null, loc);
    }

    /**
     * Create a new generic UIElement. Position is relative to any parent element.
     * Automatically adds itself to the parent node.
     * 
     * @param parent
     * @param loc
     */
    public UIElement(UIElement parent, Point loc) {
        this.location = loc;

        if (parent != null)
            parent.addChild(this);
    }

    public final void transform(Point other) {
        this.location.translate(other.x, other.y);
    }

    public final void setPosition(Point newLoc) {
        this.location = newLoc;
    }

    public final Point getLocation() {
        return new Point(this.location);
    }

    public final void tickAll() {
        this.tick();

        for (UIElement child : children)
            child.tickAll();
    }

    public final void drawAll(Point localOrigin, Graphics g) {
        Point globalPos = new Point(localOrigin);
        globalPos.translate(location.x, location.y);

        this.draw(globalPos, g);

        for (UIElement child : children) {
            child.drawAll(globalPos, g);
        }

        this.drawAfter(globalPos, g);
    }

    public final void addChild(UIElement child) {
        this.children.add(child);
    }

    protected abstract void tick();
    protected abstract void draw(Point drawLoc, Graphics g);

    /**
     * Called after all children are composited.
     * @param drawLoc
     * @param g
     */
    protected void drawAfter(Point drawLoc, Graphics g) {}
}
