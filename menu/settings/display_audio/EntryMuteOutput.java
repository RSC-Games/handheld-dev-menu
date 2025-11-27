package menu.settings.display_audio;

import java.awt.Color;
import java.awt.Point;

import backend.audio.AudioBackend;
import menu.MenuEntry;
import menu.MenuOptionList;
import ui.UIKeyValueText;

class EntryMuteOutput extends MenuEntry {
    private static final int FRAMES_UPDATE = 30;

    UIKeyValueText onOff;
    int timer = 0;

    boolean muted;

    public EntryMuteOutput(MenuOptionList parent) {
        super(parent, "", Color.WHITE, 12);
        this.onOff = new UIKeyValueText(this, new Point(), Color.white);
        this.onOff.setKeyText("Output Muted");
        this.muted = AudioBackend.getMuted();
    }

    public void menuTick() {
        if (timer-- > 0)
            return;

        this.muted = AudioBackend.getMuted();
        this.onOff.setValueText(!muted ? "No" : "Yes");
        this.onOff.setValueColor(!muted ? Color.green : Color.red);

        timer = FRAMES_UPDATE;
    }

    @Override
    public void execute() {
        AudioBackend.setMute(!this.muted);
        System.out.println("is muted: " + AudioBackend.getMuted());
    }
}
