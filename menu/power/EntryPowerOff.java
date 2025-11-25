package menu.power;

import java.awt.Color;
import java.io.IOException;

import menu.MenuEntry;
import menu.MenuOptionList;

class EntryPowerOff extends MenuEntry {

    public EntryPowerOff(MenuOptionList parent) {
        super(parent, "Power Off", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        System.out.println("Need to execute \"systemctl poweroff\"");

        try {
            Runtime.getRuntime().exec(new String[] {"systemctl", "poweroff"});
        }
        catch (IOException ie) {
            //.. doesn't really matter?
        }
    }
}
