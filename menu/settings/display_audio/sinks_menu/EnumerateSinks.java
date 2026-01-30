package menu.settings.display_audio.sinks_menu;

import java.awt.Graphics;
import java.awt.Point;

import backend.audio.AudioBackend;
import backend.audio.AudioSink;
import menu.MenuOptionList;
import menu.action_panel.DefaultActionElement;
import menu.action_panel.NotificationActionPanel;
import system.PanelManager;
import ui.UIElement;

class EnumerateSinks extends UIElement {
    boolean pushErrorPanel = false;

    public EnumerateSinks(UIElement parent, MenuOptionList menu) {
        super(parent, new Point());

        AudioSink[] sinks = AudioBackend.listSinks();

        for (AudioSink sink : sinks)
            new SinkEntry(menu, sink);

        if (sinks.length == 0)
            pushErrorPanel = true;
    }

    @Override
    protected void tick() {
        if (pushErrorPanel) {
            PanelManager.getPanelManager().pushPanel(new NotificationActionPanel(
                "No sinks are installed/present!", 
                new DefaultActionElement(2)
            ));
        }
    }

    @Override
    protected void draw(Point drawLoc, Graphics g) {}
}
