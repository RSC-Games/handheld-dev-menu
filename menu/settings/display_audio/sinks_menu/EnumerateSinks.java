package menu.settings.display_audio.sinks_menu;

import backend.audio.AudioBackend;
import backend.audio.AudioSink;
import menu.MenuOptionList;

class EnumerateSinks {
    public EnumerateSinks(MenuOptionList menu) {
        AudioSink[] sinks = AudioBackend.listSinks();

        for (AudioSink sink : sinks)
            new SinkEntry(menu, sink);

        // TODO: If no sinks are given, show a warning panel instead.
    }
}
