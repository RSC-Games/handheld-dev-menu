package menu.settings.display_audio.sinks_menu;

import java.awt.Color;

import backend.audio.AudioBackend;
import backend.audio.AudioSink;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.ActionableElement;
import menu.action_panel.ConfirmActionPanel;
import menu.action_panel.DefaultActionElement;
import system.PanelManager;

class SinkEntry extends MenuEntry {
    AudioSink sink;

    public SinkEntry(MenuOptionList parent, AudioSink sink) {
        super(parent, (sink.isDefault ? "* " : "") + sink.name, 
              Color.white, 12);
        this.sink = sink;
    }

    @Override
    public void menuTick() {}

    @Override
    public void execute() {
        PanelManager.getPanelManager().pushPanel(new ConfirmActionPanel(
            String.format("Set %s as the default audio sink?", sink.name), 
            // Yes event
            new ActionableElement() {
                protected void trigger() {
                    AudioBackend.setDefaultSink(sink);

                    PanelManager.getPanelManager().popPanel();
                    PanelManager.getPanelManager().popPanel();
                }
            }, 
            // No event
            new DefaultActionElement(3)
        ));
    }
}
