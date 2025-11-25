package menu.main;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.games.GamesMenu;
import system.PanelManager;

class EntryGames extends MenuEntry {

    public EntryGames(MenuOptionList parent) {
        super(parent, "Installed Titles", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new GamesMenu());
    }
}
