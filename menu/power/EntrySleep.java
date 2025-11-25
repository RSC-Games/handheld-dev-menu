package menu.power;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;

class EntrySleep extends MenuEntry {

    public EntrySleep(MenuOptionList parent) {
        super(parent, "Enter Sleep Mode", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        // TODO: Sleep mode is the job of the embedded controller.
        System.out.println("Not implemented; should tell EC to prepare for sleep mode");
        System.out.println("then execute \"systemctl idle\"");
    }
}
