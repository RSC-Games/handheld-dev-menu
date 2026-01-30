package menu.settings.display_audio;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.settings.display_audio.sinks_menu.AudioSinksMenu;
import system.PanelManager;

class EntryAudioEndpoint extends MenuEntry {
    // TODO: Implement audio source detection.  
    // https://wiki.archlinux.org/title/WirePlumber (wpctl)
    public EntryAudioEndpoint(MenuOptionList parent) {
        super(parent, "Audio Output Device", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new AudioSinksMenu());
    }
}
