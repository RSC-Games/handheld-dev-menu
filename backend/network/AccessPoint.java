package backend.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Log;

public class AccessPoint {
    public static final int FLAG_ESS        = 0b1 << 0;
    public static final int FLAG_IBSS       = 0b1 << 1;
    public static final int FLAG_WEP        = 0b1 << 2;
    public static final int FLAG_WPA_PSK    = 0b1 << 3;
    public static final int FLAG_WPA2_PSK   = 0b1 << 4;
    public static final int FLAG_WPA3_SAE   = 0b1 << 5;
    public static final int FLAG_CCMP       = 0b1 << 6;
    public static final int FLAG_TKIP       = 0b1 << 7;
    public static final int FLAG_SAE_H2E    = 0b1 << 8;
    public static final int FLAG_WPS        = 0b1 << 9;
    public static final int FLAG_P2P        = 0b1 << 10;
    public static final int FLAG_UNKNOWN    = 0b1 << 32;

    static final Pattern flagMatchPattern = Pattern.compile("\\[[A-Z1-9\\-]+\\]");

    private static final HashMap<String, Integer> stringToFlags;

    static {
        stringToFlags = new HashMap<>();

        stringToFlags.put("ESS", FLAG_ESS);
        stringToFlags.put("WPA-PSK-TKIP", FLAG_WPA_PSK | FLAG_TKIP);
        stringToFlags.put("WPA-PSK-CCMP", FLAG_WPA_PSK | FLAG_CCMP);
        stringToFlags.put("WPA2-PSK-CCMP", FLAG_WPA2_PSK | FLAG_CCMP);
        stringToFlags.put("WPA2-PSK+SAE-CCMP", FLAG_WPA3_SAE | FLAG_SAE_H2E);
        stringToFlags.put("SAE-H2E", FLAG_SAE_H2E);
        stringToFlags.put("WPA3-SAE-CCMP", FLAG_WPA3_SAE | FLAG_SAE_H2E);
        //stringToFlags.put("WPA-EAP-TKIP", FLAG_WPA_)  // Never added support for EAP networks.
        stringToFlags.put("WEP", FLAG_WEP);
        stringToFlags.put("IBSS", FLAG_IBSS);
        stringToFlags.put("WPS", FLAG_WPS);
    }

    public final String bssid;
    public final String ssid;
    public final int frequency;
    public final int rssi;
    public final int flags;
    private final String plaintextFlags;

    public AccessPoint(String bssid, String ssid, int freq, int rssi, String flags) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.frequency = freq;
        this.rssi = rssi;
        this.flags = parseFlags(flags);
        this.plaintextFlags = flags;
    }

    /**
     * WEP is completely unsupported and I have no way of testing it currently.
     * 
     * @return If this network uses WEP.
     */
    public boolean isWEP() {
        return getFlagsSet(FLAG_WEP);
    }

    // WPS not yet supported but can be implemented with
    // wpa_cli wps_pbc (pin-based auth won't be supported)
    public boolean WPSAvailable() {
        return getFlagsSet(FLAG_WPS);
    }

    public boolean needsAuthentication() {
        return getFlagsSet(FLAG_WPA_PSK | FLAG_WPA2_PSK | FLAG_WPA3_SAE | FLAG_TKIP | FLAG_CCMP);
    }

    public String getPlaintextFlags() {
        return plaintextFlags;
    }

    /**
     * Determine if the provided flags are set. Multiple flags can be OR'ed
     * together for this test, so combinations are possible.
     * 
     * @param mask Flags to test.
     * @return If any of the provided flags are set.
     */
    public boolean getFlagsSet(int mask) {
        return (flags & mask) != 0;
    }

    /**
     * Determine if all of the provided flags in the mask are set.
     * 
     * @param mask All flags to test
     * @return True iff all of the queried flags are set.
     */
    public boolean allFlagsSet(int mask) {
        return (flags & mask) == mask;
    }

    /**
     * Determine if the provided flag is the only flag set. Multiple flags
     * can be OR'ed together for this test, so combinations of flags can be 
     * tested.
     * 
     * @param mask Determine if these flags are the only flags set.
     * @return True iff these flags are the only ones set and all are set.
     */
    public boolean areOnlyFlagsSet(int mask) {
        return (flags & ~mask) == 0 && allFlagsSet(mask);
    }

    /**
     * Parse the human readable flags into a machine-readable, maskable
     * integer for easier parsing.
     * 
     * @param flags The full, unprocessed string of flags
     * @return An integer representing all of the present flags.
     */
    private static int parseFlags(String flags) {
        // Regex: "[A-Z\\-]+"
        Matcher flagsMatcher = flagMatchPattern.matcher(flags);
        ArrayList<String> parsedStringFlags = new ArrayList<>();
        
        // Get each flag and strip off the enclosing brackets.
        while (flagsMatcher.find()) {
            String foundFlag = flagsMatcher.group();
            parsedStringFlags.add(foundFlag.substring(1, foundFlag.length() - 1));
        }

        int outFlag = 0;

        for (String flag : parsedStringFlags) {
            if (!stringToFlags.containsKey(flag))
                Log.logWarning("network_backend.ap: got unknown flag: " + flag);

            outFlag |= stringToFlags.getOrDefault(flag, FLAG_UNKNOWN);
        }

        return outFlag;
    }
}
