package menu.overlays.quick_settings.quick_settings;

import menu.MenuEntrySlider;
import menu.MenuOptionList;

class EntryDisplayDimSlider extends MenuEntrySlider {

    // TODO: Actually implement the dim timer.
    // Can sleep up to 60 minutes.
    public EntryDisplayDimSlider(MenuOptionList parent) {
        super(parent, "Display Dim Timeout", "m", 1, 60, true);
    }

    @Override
    protected void valueChanged(int newValue) {
        // TODO: Value change doesn't do anything yet.
    }
    
}
