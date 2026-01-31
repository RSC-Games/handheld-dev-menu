package menu.settings.perf_power_mgmt;

import backend.BacklightManagement;
import menu.MenuEntrySlider;
import menu.MenuOptionList;

class EntrySleepModeSlider extends MenuEntrySlider {
    BacklightManagement backlightService = BacklightManagement.getBacklightService();
    
    // Sleep can be done anywhere from 5-300 minutes.
    public EntrySleepModeSlider(MenuOptionList parent) {
        super(parent, "Sleep Mode Timeout", "m", 5, 300, true);
        this.setSliderValue(backlightService.getSleepTimeout());
    }

    @Override
    protected void valueChanged(int newValue) {
        backlightService.setSleepTimeout(newValue);
    }
    
}
