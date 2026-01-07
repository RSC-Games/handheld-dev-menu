package menu.games;

import java.awt.Point;

import backend.TitleLaunchService;
import menu.action_panel.NotificationActionPanel;
import menu.action_panel.DefaultActionElement;
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

        NotificationActionPanel panel;
        DefaultActionElement sharedElement = new DefaultActionElement();

        // Show the appropriate message depending on exit state.
        if (exitCode != 0) {
            panel = new NotificationActionPanel("The title has closed because an error occurred (code " + exitCode + ")",
                sharedElement
            );
        }
        else
            panel = new NotificationActionPanel("The title has been closed successfully.", sharedElement);

        PanelManager.getPanelManager().pushPanel(panel);
    }
}
