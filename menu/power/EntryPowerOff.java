package menu.power;

import java.awt.Color;
import java.io.IOException;

import menu.MenuEntry;
import menu.MenuOptionList;
import system.Config;
import util.Log;

class EntryPowerOff extends MenuEntry {

    public EntryPowerOff(MenuOptionList parent) {
        super(parent, "Power Off", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        if (!Config.ENABLE_POWER_MANAGEMENT) {
            Log.logInfo("menu.pm: power management requested but disabled");
            return;
        }

        try {
            Runtime.getRuntime().exec(new String[] {"systemctl", "poweroff"});
            Log.logVerbose("menu.pm: executing \"systemctl poweroff\"");
        }
        catch (IOException ie) {
            //.. doesn't really matter?
        }
    }
}
