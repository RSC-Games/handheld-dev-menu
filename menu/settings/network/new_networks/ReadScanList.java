package menu.settings.network.new_networks;

import java.awt.Point;

import backend.NetworkBackend;
import backend.network.AccessPoint;
import menu.MenuOptionList;
import ui.UIBase;
import ui.UIElement;

class ReadScanList extends UIBase {
    static final int TIMER_FRAMES = 300;

    int timer = TIMER_FRAMES;
    MenuOptionList attachedMenu;

    public ReadScanList(UIElement parent, MenuOptionList menu) {
        super(parent, new Point());
        this.attachedMenu = menu;
        generateMenu();
    }

    @Override
    protected void tick() {
        if (timer-- > 0)
            return;

        generateMenu();
        timer = TIMER_FRAMES;
    }

    private void generateMenu() {
        AccessPoint[] aps = NetworkBackend.getScanResults();

        if (aps.length == 0)
            new NoAccessPointsInRangeEntry(attachedMenu);

        for (AccessPoint ap : aps) {
            if (ap == null)
                continue;

            new AccessPointMenuEntry(attachedMenu, ap);
        }
    }
}
