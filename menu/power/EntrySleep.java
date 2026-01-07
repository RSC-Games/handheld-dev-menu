package menu.power;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import system.Config;
import util.Log;

class EntrySleep extends MenuEntry {

    public EntrySleep(MenuOptionList parent) {
        super(parent, "Enter Sleep Mode", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        if (!Config.ENABLE_POWER_MANAGEMENT) {
            Log.logInfo("menu.pm: power management requested but disabled");
            return;
        }

        // TODO: Sleep mode is the job of the embedded controller.
        Log.logWarning("pm.sleep: Not implemented; should tell EC to prepare for sleep mode");
        // then execute \"systemctl idle\"
    }
}
