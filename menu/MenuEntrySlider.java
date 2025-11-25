package menu;

import java.awt.Color;
import java.awt.Point;

import system.InputManager;
import ui.UIKeyValueText;

public abstract class MenuEntrySlider extends MenuEntry {
    private ScrollTimer stateTimer;
    private boolean enableWraparound;

    UIKeyValueText textBox;
    String postfix;
    int maxValue;
    int minValue;
    int curValue;

    public MenuEntrySlider(MenuOptionList parent, String text, String postfix, int minVal, 
                           int maxVal, boolean enableWraparound) {
        super(parent, "", Color.white, 12);
        this.textBox = new UIKeyValueText(this, new Point(), Color.white);
        this.textBox.setKeyText(text);
        this.textBox.setValueText("<inv>");
        this.stateTimer = new ScrollTimer(300, "inactive");
        this.enableWraparound = enableWraparound;
        this.postfix = postfix;

        this.minValue = minVal;
        this.maxValue = maxVal;
        this.curValue = minVal;

        setSliderValue(this.curValue);
    }

    @Override
    public void menuTick() {
        if (!isSelected())
            return;

        // Scroll acceleration.
        int xAxis = InputManager.getInputManager().getXAxis();
        String curState = getInputState(xAxis);
        boolean isNewState = this.stateTimer.resetIfDifferentState(curState);

        // Avoid switching options every frame.
        if (!isNewState && !stateTimer.tick()) 
            return;

        // Instantly reset the timer to prevent sticking when buttons are changed.
        if (isNewState && curState.equals("inactive")) {
            stateTimer.expire();
            return;
        }

        // No action, nothing to do.
        if (xAxis == 0)
            return;

        // Some sliders should wrap around... other ones shouldn't (like volume)
        if (this.enableWraparound) {
            this.curValue += xAxis;
            this.curValue = this.curValue >= this.minValue 
                ? Math.max(this.minValue, this.curValue % (this.maxValue + 1))
                : this.maxValue;
        }
        else {
            int newValue = this.curValue + xAxis;
            this.curValue = Math.max(Math.min(this.maxValue, newValue), this.minValue);

            // Hit a boundary of some sort, so no point in refreshing the slider.
            if (newValue != this.curValue)
                return;
        }

        valueChanged(this.curValue);
        setSliderValue(this.curValue);
    } 

    protected void setSliderValue(int newValue) {
        this.curValue = Math.max(Math.min(this.maxValue, newValue), this.minValue);

        String leftCaret = !enableWraparound && curValue == this.minValue ? "" : "<";
        String rightCaret = !enableWraparound && curValue == this.maxValue ? "" : ">";
        this.textBox.setValueText(String.format("%s %d%s %s", leftCaret, curValue, postfix, rightCaret));
    }

    private String getInputState(int xAxis) {     
        switch (xAxis) {
            case 1:
                return "right";
            case -1:
                return "left";
        }

        return "inactive";
    }

    @Override
    public void execute() {}

    /**
     * Fired once every time the slider value changes.
     * 
     * @param newValue The new value the slider changed to.
     */
    protected abstract void valueChanged(int newValue);
}
