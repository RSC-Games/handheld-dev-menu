package menu.settings.display_audio;

import backend.audio.AudioBackend;
import menu.MenuEntrySlider;
import menu.MenuOptionList;

class EntryOutputVolume extends MenuEntrySlider {
    private static final int FRAMES_UPDATE = 30;

    int timer = 0;

    public EntryOutputVolume(MenuOptionList parent) {
        super(parent, "Output Volume", "%", 0, 100, false);
        this.setSliderValue(AudioBackend.getVolume()); // whatever the default is
    }

    @Override
    public void menuTick() {
        super.menuTick();

        // Avoid updating the slider every frame (command execution has quite a bit of latency)
        if (timer-- > 0)
            return;

        this.setSliderValue(AudioBackend.getVolume());
        timer = FRAMES_UPDATE;
    }

    @Override
    protected void valueChanged(int newValue) {
        AudioBackend.setVolume(newValue);
    }
    
}
