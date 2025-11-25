package backend.network;

import java.util.HashMap;
import java.util.regex.Matcher;

public class SavedNetwork {
    public enum NetworkState {
        ACTIVE,
        DISABLED,
        TEMP_DISABLED,
        CURRENT
    }

    private static final HashMap<String, NetworkState> stringToState;

    static {
        stringToState = new HashMap<>();

        stringToState.put("DISABLED", NetworkState.DISABLED);
        stringToState.put("TEMP-DISABLED", NetworkState.TEMP_DISABLED);
        stringToState.put("CURRENT", NetworkState.CURRENT);
    }

    public final int networkID;
    public final String ssid;
    public final String bssid;
    public final NetworkState state;

    public SavedNetwork(int networkID, String ssid, String bssid, String state) {
        this.networkID = networkID;
        this.ssid = ssid;
        this.bssid = bssid;
        this.state = parseState(state);
    }

    /**
     * Parse the human readable flags into a machine-readable, maskable
     * integer for easier parsing.
     * 
     * @param flags The full, unprocessed string of flags
     * @return An integer representing all of the present flags.
     */
    private static NetworkState parseState(String networkState) {
        // We borrow the regex from AccessPoint to avoid redefining it twice.
        Matcher stateMatcher = AccessPoint.flagMatchPattern.matcher(networkState);

        // No flag means network is active but not scanning or connected.
        if (!stateMatcher.find())
            return NetworkState.ACTIVE;

        String foundState = stateMatcher.group();
        return stringToState.getOrDefault(foundState.substring(1, foundState.length() - 1), 
                                          NetworkState.ACTIVE);
    }
}
