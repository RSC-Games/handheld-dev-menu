package menu.power;

import java.awt.Color;
import java.io.IOException;

import menu.MenuEntry;
import menu.MenuOptionList;

class EntryReboot extends MenuEntry {

    public EntryReboot(MenuOptionList parent) {
        super(parent, "Reboot", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        System.out.println("Need to execute \"systemctl reboot\"");

        try {
            Runtime.getRuntime().exec(new String[] {"systemctl", "reboot"});
        }
        catch (IOException ie) {
            //.. doesn't really matter?
        }
    }
}
