package menu.games;

import java.awt.Point;

import backend.TitleLaunchService;
import menu.action_panel.ActionPanel;
import menu.action_panel.ActionableElement;
import system.InputManager;
import system.PanelManager;
import ui.UIBase;
import ui.UIElement;

class PopUpIfGameQuit extends UIBase {
    public PopUpIfGameQuit(UIElement parent) {
        super(parent, new Point());
    }

    @Override
    public void tick() {
        InputManager inputSystem = InputManager.getInputManager();

        // Allow closing the application from the menu.
        if (inputSystem.edit() && TitleLaunchService.isTitleRunning())
            TitleLaunchService.terminate();

        if (TitleLaunchService.isTitleRunning() || !TitleLaunchService.processWaitingForReap())
            return;

        reapProcessAndNotify();
    }

    private void reapProcessAndNotify() {
        int exitCode = TitleLaunchService.getExitCode();

        ActionPanel panel;
        ActionableElement sharedElement = new ActionableElement() {
            protected void trigger() {
                PanelManager.getPanelManager().popPanel();
            }
        };

        // Show the appropriate message depending on exit state.
        if (exitCode != 0) {
            panel = new ActionPanel("The title has closed because an error occurred (code " + exitCode + ")",
                sharedElement
            );
        }
        else
            panel = new ActionPanel("The title has been closed successfully.", sharedElement);

        PanelManager.getPanelManager().pushPanel(panel);
    }
}
