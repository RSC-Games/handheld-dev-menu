package menu.overlays.quick_settings.quick_settings;

import menu.MenuEntrySlider;
import menu.MenuOptionList;

class EntrySleepModeSlider extends MenuEntrySlider {
    
    // TODO: Actually implement the sleep timer.
    // Sleep can be done anywhere from 5-300 minutes.
    public EntrySleepModeSlider(MenuOptionList parent) {
        super(parent, "Sleep Mode Timeout", "m", 5, 300, true);
    }

    @Override
    protected void valueChanged(int newValue) {
        // TODO: Value change doesn't do anything yet.
    }
    
}
