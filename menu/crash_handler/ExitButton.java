package menu.crash_handler;

import java.awt.Point;

import system.InputManager;
import system.PanelManager;
import ui.UIBase;
import ui.UIElement;

public class ExitButton extends UIBase {
    public ExitButton(UIElement parent) {
        super(parent, new Point());
    }

    @Override
    protected void tick() {
        // Only menu running (emptying the stack terminates the app)
        if (InputManager.getInputManager().confirm())
            PanelManager.getPanelManager().popPanel();
    }
}
