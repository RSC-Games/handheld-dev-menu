package menu.settings.perf_power_mgmt;

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
