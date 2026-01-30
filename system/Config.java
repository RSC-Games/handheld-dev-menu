package system;

import java.util.ArrayList;
import java.util.Arrays;

import util.Log.LogLevel;

public class Config {
    public static final LogLevel INITIAL_LOG_LEVEL = LogLevel.INFO;
    public static final boolean LOG_HANDLED_EXCEPTIONS = true; // TODO: Implement.
    public static final boolean SHOW_COMMAND_ARGS_IN_LOGS = true;

    public static final boolean ENABLE_NETWORK_BACKEND = true; // false for development
    public static final boolean ENABLE_PIPEWIRE = true; // TODO: implement
    public static final boolean ENABLE_POWER_MANAGEMENT = true; // false for development

    public static final ArrayList<String> SUPPRESS_CMD_ERRLOG = new ArrayList<>(Arrays.asList(new String[] {
        "vcgencmd", "cat", "rfkill"
    }));
}
