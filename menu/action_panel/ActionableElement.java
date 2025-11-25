package menu.action_panel;

import java.awt.Point;

import system.InputManager;
import ui.UIBase;

public abstract class ActionableElement extends UIBase {
    public ActionableElement() {
        super(null, new Point());
    }

    @Override
    protected void tick() {
        // Go back a menu (if possible)
        if (InputManager.getInputManager().confirm())
            trigger();
    }

    protected abstract void trigger();
}
