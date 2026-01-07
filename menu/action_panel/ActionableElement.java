package menu.action_panel;

import java.awt.Color;

import menu.MenuEntry;

public abstract class ActionableElement extends MenuEntry {
    public ActionableElement() {
        super(null, "", Color.white, 12);
    }

    public void execute() {
        trigger();
    }

    protected abstract void trigger();
}
