package menu.settings.perf_power_mgmt;

import java.awt.Color;
import java.awt.Point;

import menu.MenuEntry;
import menu.MenuOptionList;
import ui.UIKeyValueText;

class EntryPerformanceOverlay extends MenuEntry {
    boolean isEnabled = false;
    UIKeyValueText uiText;

    // TODO: Need to actually enable/disable the performance overlay.
    public EntryPerformanceOverlay(MenuOptionList parent) {
        super(parent, "", Color.white, 12);
        this.uiText = new UIKeyValueText(this, new Point(), Color.white);
        this.uiText.setKeyText("Performance Overlay Enabled");
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
