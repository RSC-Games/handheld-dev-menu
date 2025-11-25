package menu.settings;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;

class EntryTitleManagement extends MenuEntry {

    public EntryTitleManagement(MenuOptionList parent) {
        super(parent, "Title Management", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
        //PanelManager manager = PanelManager.getPanelManager();
        //manager.pushPanel(new PowerMenu());
    }
}
