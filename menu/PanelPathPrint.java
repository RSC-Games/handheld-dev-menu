package menu;

import java.awt.Color;
import java.awt.Point;

import system.PanelManager;
import ui.UIElement;
import ui.UIText;

public class PanelPathPrint extends UIText {
    static final String PATH_ROOT = "@: ";
    PanelManager manager = PanelManager.getPanelManager();

    public PanelPathPrint(UIElement parent, Point loc) {
        super(parent, loc, "@: ", Color.WHITE, 12);
    }

    @Override
    public void tick() {
        this.text = PATH_ROOT + manager.getPanelPath();
    }
}
