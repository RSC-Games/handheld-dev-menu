package menu;

import java.awt.Graphics;
import java.awt.Point;

import system.InputManager;
import ui.UIElement;

public class MenuOptionList extends UIElement {
    static final int FONT_SIZE = 12;
    static final float LERP_CONSTANT = 0.2f;

    int menuOptions = 0;
    int curMenuOption = 0;
    ScrollTimer stateChangeTimer;
    Point originalOffset;
    Point target;

    // Points are ints and they don't retain the precision we need.
    float curLerpY;

    public MenuOptionList(UIElement parent, Point loc) {
        super(parent, loc);
        stateChangeTimer = new ScrollTimer(300, "inactive");
        this.originalOffset = new Point(loc);
        this.target = loc;
    }

    /**
     * Provided text should be at the local origin relative to this system.
     * The offset is calculated manually from the index of this object.
     * Does not implicity add the provided menu option as a child!
     * @param entry
     */
    public void addMenuOption(MenuEntry entry) {
        entry.register(new Point(0, menuOptions++ * FONT_SIZE));

        // Select first option (since it's not done in the tick function).
        if (menuOptions == 1)
            entry.select();
    }

    /**
     * Clear all menu options. Useful for dynamically generated menus.
     */
    public void clearMenuOptions() {
        this.children.clear();
    }

    @Override
    protected void tick() {
        if (this.children.size() == 0)
            return;

        // See below.
        int menuIndex = Math.round(-(this.location.y - this.originalOffset.y) / (float)FONT_SIZE);
        ((MenuEntry)this.children.get(menuIndex)).unselect();

        // Animations.
        this.curLerpY = lerpY(this.location, this.target, LERP_CONSTANT);
        this.location = new Point(this.location.x, Math.round(this.curLerpY));

        if (Math.abs(this.curLerpY - this.target.y) < 2)
            this.location.y = this.target.y;

        // Track the active button (animation-wise, does not affect which one is executed).
        menuIndex = Math.round(-(this.location.y - this.originalOffset.y) / (float)FONT_SIZE);
        ((MenuEntry)this.children.get(menuIndex)).select();

        InputManager manager = InputManager.getInputManager();

        // Allow navigation.
        if (manager.confirm()) {
            ((MenuEntry)this.children.get(curMenuOption)).execute();
            return;
        }

        // State management.
        int yAxis = manager.getYAxis();
        String newState = getInputState(yAxis);
        boolean stateChanged = stateChangeTimer.resetIfDifferentState(newState);

        // Avoid switching options every frame.
        if (!stateChanged && !stateChangeTimer.tick()) 
            return;

        // Instantly reset the timer to prevent sticking when buttons are changed.
        if (stateChanged && newState.equals("inactive")) {
            stateChangeTimer.expire();
            return;
        }

        // Don't waste time on smoothing when there's no change in state.
        if (yAxis == 0)
            return;

        // Axis inverted relative to indexing (higher indexing goes down the list).
        curMenuOption -= yAxis;
        curMenuOption = curMenuOption >= 0 ? curMenuOption % menuOptions : menuOptions - 1;

        // Set new target
        Point newTarget = new Point(0, -curMenuOption * FONT_SIZE);
        newTarget.translate(originalOffset.x, originalOffset.y);
        target = newTarget;
    }

    static float lerpY(Point start, Point end, float t) {
        float a = start.y;
        float b = end.y;
        return a + t * (b - a);
    }

    private String getInputState(int yAxis) {
        //int xAxis = manager.getXAxis();
        //int yAxis = manager.getYAxis();

        /*
        switch (xAxis) {
            case 1:
                return "right";
            case -1:
                return "left";
        }*/

        // Vertical menu system; only needs Y axis.
        switch (yAxis) {
            case 1:
                return "up";
            case -1:
                return "down";
        }

        return "inactive";
    }

    @Override
    protected void draw(Point drawLoc, Graphics g) {}
    
}