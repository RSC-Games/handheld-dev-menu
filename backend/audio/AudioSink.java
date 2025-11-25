package backend.audio;

public class AudioSink {
    public final boolean isDefault;
    public final int id;
    public final String name;

    AudioSink(boolean isDefault, int id, String name) {
        this.isDefault = isDefault;
        this.id = id;
        this.name = name;
    }
}
