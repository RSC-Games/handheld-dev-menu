package menu.power;

import java.awt.Color;
import java.io.IOException;

import menu.MenuEntry;
import menu.MenuOptionList;
import system.Config;
import util.Log;

class EntryReboot extends MenuEntry {

    public EntryReboot(MenuOptionList parent) {
        super(parent, "Reboot", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        if (!Config.ENABLE_POWER_MANAGEMENT) {
            Log.logInfo("menu.pm: power management requested but disabled");
            return;
        }

        try {
            Log.logVerbose("menu.pm: executing \"systemctl reboot\"");
            Runtime.getRuntime().exec(new String[] {"systemctl", "reboot"});
        }
        catch (IOException ie) {
            //.. doesn't really matter?
        }
    }
}
