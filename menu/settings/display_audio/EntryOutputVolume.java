package menu.settings.display_audio;

import menu.MenuEntrySlider;
import menu.MenuOptionList;

class EntryOutputVolume extends MenuEntrySlider {

    // TODO: Implement audio management.
    public EntryOutputVolume(MenuOptionList parent) {
        super(parent, "Output Volume", " [NOT_IMPL]", 1, 100, false);
        this.setSliderValue(0); // whatever the default is
    }

    @Override
    protected void valueChanged(int newValue) {
        //BacklightService.setScaledBrightness(newValue);
    }
    
}
