package menu.overlays.quick_settings.quick_settings;

import java.awt.Color;
import java.awt.Point;

import menu.MenuEntry;
import menu.MenuOptionList;
import ui.UIKeyValueText;

class EntryEnableThrottling extends MenuEntry {
    // TODO: Need a parameter store somewhere but we don't have one.
    boolean isEnabled = false;
    UIKeyValueText uiText;

    // TODO: Do these below things:
    //      (Drops the CPU clock from 2.1 GHz to 1.8 GHz and drops GPU clock from peak to 750 MHz)
    //      (These are minimums so if overclocking isn't already done then it won't drop)
    //      sudo cp /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq
    public EntryEnableThrottling(MenuOptionList parent) {
        super(parent, "", Color.white, 12);
        this.uiText = new UIKeyValueText(this, new Point(), Color.white);
        this.uiText.setKeyText("Throttle when Low Battery (<= 5%)");
    }

    @Override
    public void menuTick() {
        this.uiText.setValueText(isEnabled ? "Yes" : "No");
        this.uiText.setValueColor(isEnabled ? Color.green : Color.red);
    }

    @Override
    public void execute() {
        isEnabled = !isEnabled;
    }
    
}
