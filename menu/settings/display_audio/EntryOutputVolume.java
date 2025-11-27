package menu.settings.display_audio;

import backend.audio.AudioBackend;
import menu.MenuEntrySlider;
import menu.MenuOptionList;

class EntryOutputVolume extends MenuEntrySlider {
    public EntryOutputVolume(MenuOptionList parent) {
        super(parent, "Output Volume", "%", 0, 100, false);
        this.setSliderValue(AudioBackend.getVolume()); // whatever the default is
    }

    @Override
    protected void valueChanged(int newValue) {
        AudioBackend.setVolume(newValue);
    }
    
}
