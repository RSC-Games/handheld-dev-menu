package menu;

import java.awt.Point;

import system.InputManager;
import system.PanelManager;
import ui.UIBase;
import ui.UIElement;

public class BackButton extends UIBase {
    public BackButton(UIElement parent) {
        super(parent, new Point());
    }

    @Override
    protected void tick() {
        // Go back a menu (if possible)
        if (InputManager.getInputManager().back())
            PanelManager.getPanelManager().popPanel();
    }   
}
