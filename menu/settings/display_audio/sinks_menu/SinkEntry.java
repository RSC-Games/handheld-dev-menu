package menu.settings.display_audio.sinks_menu;

import java.awt.Color;

import backend.audio.AudioSink;
import menu.MenuEntry;
import menu.MenuOptionList;
import menu.action_panel.NotificationActionPanel;
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
        //PanelManager.getPanelManager().pushPanel(panel);
    }
    
}
