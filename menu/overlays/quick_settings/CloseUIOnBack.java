package menu.overlays.quick_settings;

import java.awt.Graphics;
import java.awt.Point;

import system.InputManager;
import system.MenuOverlayWindow;
import ui.UIElement;

public class CloseUIOnBack extends UIElement {
    public CloseUIOnBack(UIElement parent) {
        super(parent, new Point());
    }

    @Override
    protected void tick() {
        if (InputManager.getInputManager().back())
            MenuOverlayWindow.getWindow().hide();
    }

    @Override
    protected void draw(Point drawLoc, Graphics g) {}
    
}
