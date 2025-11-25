package menu.games;

import java.io.File;

import backend.TitleLaunchService;
import backend.title.TitleInfo;
import menu.MenuOptionList;

class GenerateGamesList {
    public GenerateGamesList(MenuOptionList menu) {
        TitleInfo[] titles = TitleLaunchService.getTitleList(new File("./titles.csv"));

        for (TitleInfo title : titles) {
            if (title == null)
                continue;

            new GameMenuEntry(menu, title);
        }
    }
}
