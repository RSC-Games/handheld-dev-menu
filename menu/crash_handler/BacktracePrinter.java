package menu.crash_handler;

import java.awt.Color;
import java.awt.Point;

import ui.UIBase;
import ui.UIElement;
import ui.UIText;

public class BacktracePrinter extends UIBase {
    public BacktracePrinter(UIElement parent, Point offset, String[] backtrace) {
        super(parent, offset);

        for (int i = 0; i < backtrace.length; i++)
            new UIText(this, new Point(0, i * 15), backtrace[i], Color.yellow, 12);
    }
}
