package menu.settings.perf_power_mgmt;

import java.awt.Color;
import java.awt.Point;

import backend.NetworkBackend;
import menu.MenuEntry;
import menu.MenuOptionList;
import ui.UIKeyValueText;

class EntryEnableWiFi extends MenuEntry {
    private static final int FRAMES_UPDATE = 30;

    boolean wifiEnabled = true;
    UIKeyValueText onOff;
    int timer = 0;

    public EntryEnableWiFi(MenuOptionList parent) {
        super(parent, "", Color.WHITE, 12);
        this.onOff = new UIKeyValueText(this, new Point(0, 0), Color.white);
        this.onOff.setKeyText("Enable WiFi");
    }

    public void menuTick() {
        if (timer-- > 0)
            return;

        this.wifiEnabled = NetworkBackend.wlanEnabled();
        this.onOff.setValueText(wifiEnabled ? "Yes" : "No");
        this.onOff.setValueColor(wifiEnabled ? Color.green : Color.red);

        timer = FRAMES_UPDATE;
    }

    @Override
    public void execute() {
        NetworkBackend.setWlanState(!wifiEnabled);
    }
}
