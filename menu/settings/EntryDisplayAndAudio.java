package menu.settings;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.settings.display_audio.DisplayAndAudioMenu;
import system.PanelManager;

class EntryDisplayAndAudio extends MenuEntry {

    public EntryDisplayAndAudio(MenuOptionList parent) {
        super(parent, "Display and Audio", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager manager = PanelManager.getPanelManager();
        manager.pushPanel(new DisplayAndAudioMenu());
    }
}
