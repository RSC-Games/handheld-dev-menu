package menu.settings;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.settings.perf_power_mgmt.PerformancePowerMgmtMenu;
import system.PanelManager;

class EntryPowerManagement extends MenuEntry {

    public EntryPowerManagement(MenuOptionList parent) {
        super(parent, "Performance and Power Management", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager manager = PanelManager.getPanelManager();
        manager.pushPanel(new PerformancePowerMgmtMenu());
    }
}
