package menu.settings.display_audio;

import java.awt.Color;
import java.awt.Point;

import menu.MenuEntry;
import menu.MenuOptionList;
import ui.UIKeyValueText;

class EntryMuteOutput extends MenuEntry {
    private static final int FRAMES_UPDATE = 30;

    UIKeyValueText onOff;
    int timer = 0;

    boolean broken = false;

    public EntryMuteOutput(MenuOptionList parent) {
        super(parent, "", Color.WHITE, 12);
        this.onOff = new UIKeyValueText(this, new Point(), Color.white);
        this.onOff.setKeyText("Output Muted");
    }

    public void menuTick() {
        if (timer-- > 0)
            return;

        //this.wifiEnabled = NetworkBackend.wlanEnabled();
        // TODO: Figure out alsa and pulse and how to mute programmatically
        this.onOff.setValueText(!broken ? "No" : "ERROR_NOT_IMPL");
        this.onOff.setValueColor(!broken ? Color.green : Color.orange);

        timer = FRAMES_UPDATE;
    }

    @Override
    public void execute() {
        //NetworkBackend.setWlanState(!wifiEnabled);
        this.broken = true;
    }
}
