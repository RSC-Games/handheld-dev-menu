package menu.games;

import java.awt.Color;

import backend.TitleLaunchService;
import backend.title.TitleInfo;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.ActionPanel;
import menu.action_panel.ActionableElement;
import system.PanelManager;

class GameMenuEntry extends MenuEntry {
    TitleInfo title;

    public GameMenuEntry(MenuOptionList parent, TitleInfo title) {
        super(parent, title.entryName + (TitleLaunchService.thisTitleActive(title) ? " (ACTIVE)" : ""), 
              Color.white, 12);
        this.text = String.format(title.entryName);
        this.title = title;
    }

    @Override
    public void menuTick() {

    }

    @Override
    public void execute() {
        ActionPanel panel;

        if (!TitleLaunchService.isTitleRunning()) {
            boolean launched = TitleLaunchService.launchTitle(title);
            panel = new ActionPanel(
                (launched ? "Launched title" : "Error: Failed to launch title") + " " + title.entryName, 
                new ActionableElement() {
                    protected void trigger() {
                        if (!launched)
                            PanelManager.getPanelManager().popPanel();
                    }
                }
            );

            if (launched) {
                TitleLaunchService.registerWindowHideCallback(() -> { PanelManager.getPanelManager().popPanel(); });
                TitleLaunchService.setHideTimer(2);
            }
        }
        else {
            if (TitleLaunchService.thisTitleActive(title)) {
                TitleLaunchService.setWindowHideState(true);
                return;
            }

            panel = new ActionPanel(
                "Title already running! Please close (with X) before starting a new one.", 
                new ActionableElement() {
                    protected void trigger() {
                        PanelManager.getPanelManager().popPanel();
                    }
                }
            );
        }

        PanelManager.getPanelManager().pushPanel(panel);
    }
    
}
