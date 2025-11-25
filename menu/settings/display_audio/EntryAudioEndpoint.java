package menu.settings.display_audio;

import java.awt.Color;

import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.ActionPanel;
import menu.action_panel.ActionableElement;
import system.PanelManager;

class EntryAudioEndpoint extends MenuEntry {

    // TODO: Implement audio sink/source detection.
    // https://wiki.archlinux.org/title/WirePlumber (wpctl)
    public EntryAudioEndpoint(MenuOptionList parent) {
        super(parent, "Audio Output Device", Color.WHITE, 12);
    }

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new ActionPanel("Pipewire backend not implemented",
            new ActionableElement() {
                protected void trigger() {
                    PanelManager.getPanelManager().popPanel();
                }
            }
        ));
    }
}
