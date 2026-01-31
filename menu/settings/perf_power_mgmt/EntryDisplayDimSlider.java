package menu.settings.perf_power_mgmt;

import backend.BacklightManagement;
import menu.MenuEntrySlider;
import menu.MenuOptionList;

class EntryDisplayDimSlider extends MenuEntrySlider {
    BacklightManagement backlightService = BacklightManagement.getBacklightService();

    // Can sleep up to 60 minutes.
    public EntryDisplayDimSlider(MenuOptionList parent) {
        super(parent, "Display Dim Timeout", "m", 1, 60, true);
        this.setSliderValue(backlightService.getDimTimeout());
    }

    @Override
    protected void valueChanged(int newValue) {
        backlightService.setDimTimeout(newValue);
    }
}
