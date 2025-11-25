package menu.settings.display_audio;

import backend.BacklightService;
import menu.MenuEntrySlider;
import menu.MenuOptionList;

class EntryBrightness extends MenuEntrySlider {

    public EntryBrightness(MenuOptionList parent) {
        super(parent, "Brightness", "%", 1, 100, true);
        this.setSliderValue(BacklightService.getScaledBrightness());
    }

    // TODO: Brightness load is broken.
    @Override
    protected void valueChanged(int newValue) {
        BacklightService.setScaledBrightness(newValue);
    }
    
}
