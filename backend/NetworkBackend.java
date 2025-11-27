package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import backend.network.AccessPoint;
import backend.network.SavedNetwork;
import backend.system_mgmt.SMCallbackFunction;
import backend.system_mgmt.SMStopExecution;
import backend.system_mgmt.SystemManagementThread;
import backend.CommandUtils.CommandOutput;

/**
 * High level wrapper around all of the Linux networking tools.
 */
public class NetworkBackend {
    private static final int ENTRY_NO_NETWORK = -1;

    private static int activeWlanEntry = ENTRY_NO_NETWORK;

    NetworkBackend() {}

    static {
        CommandOutput output = CommandUtils.executeCommandRetry("rfkill");

        if (output != null && output.getExitCode() == 0)
            determineWlanOffset(output.getStdout().split("\n"));
        else
            System.err.println("Failed to run rfkill! Couldn't determine wifi card");
    }

    /**
     * Figure out which rfkill entry is a WiFi card. In the event that more than
     * one is installed, it picks the first entry.
     * If no such entry is found, the offset is set to -1
     * 
     * @param lines Output from rfkill.
     */
    private static void determineWlanOffset(String[] lines) {
        // Look for a wlan entry, if any.
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].strip().split("\s+")[1].equals("wlan")) {
                activeWlanEntry = i;
                return;
            }
        }

        System.err.println("Warning: No wlan card identifiable!");
    }

    /**
     * 
     * DHCP: (now dhcpcd)
     * sudo dhcpcd --oneshot
     *  (should return 0; if 256 is returned, try reauth)
     * 
     * IP ADDRESS:
     * ip -o addr
     *  -- snip --
     *  3: wlan0 \t inet 192.168.1.3 brd .....
     *                   ^^^^^^^^^^^--- Want this segment for wlan0
     * 
     * to determine if it has an IP address just run status()
     */

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /// RFKILL FUNCTIONS BELOW!
    ///
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * RFKILL output:
     * 
     * ID TYPE DEVICE      SOFT      HARD
     *  0 wlan phy0   unblocked unblocked
     *  1 wlan phy1   unblocked unblocked
     * 
     * @return If this card is enabled or not.
     */
    //static boolean fakeEnabled = false;
    public static boolean wlanEnabled() {
        CommandOutput output = CommandUtils.executeCommandRetry("rfkill");
        // /*new CommandOutput("ID TYPE DEVICE        SOFT        HARD\n 0 wlan phy0   unblocked unblocked\n 1 wlan phy0   unblocked unblocked", null, 0)

        if (output == null || activeWlanEntry == ENTRY_NO_NETWORK)
            return false;

        String[] lines = output.getStdout().split("\n");
        String[] status = lines[activeWlanEntry].strip().split("\s+");

        // Ensure this is the first wlan object.
        if (!status[1].equals("wlan")) {
            System.out.println("unexpected device found: got " + status[1]);
            System.out.println(output.getStdout());
            return false;
        }

        return status[3].equals("unblocked"); //fakeEnabled;
    }

    /**
     * Set the active state of the attached wifi card, if one is present.
     * 
     * @param state The new rfkill state (true for enabled, false otherwise)
     * @return if the command executed properly.
     */
    public static boolean setWlanState(boolean enabled) {
        if (activeWlanEntry == ENTRY_NO_NETWORK)
            return false;

        CommandOutput output = CommandUtils.executeCommandRetry(
            "rfkill", 
            enabled ? "unblock" : "block", "" + (activeWlanEntry - 1)
        );

        if (output == null)
            return false;

        return output.getExitCode() == 0;
    }

    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /// WPA_CLI FUNCTIONS BELOW!
    ///
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Trampoline to the internal API function startScan
     * 
     * @see NetworkBackend$WpaCliPlugin.startScan()
     */
    public static boolean startScan() {
        return WpaCliPlugin.startScan();
    }

    /**
     * Trampoline to internal getScanResults()
     * @see NetworkBackend$WpaCliPlugin.getScanResults()
     */
    public static AccessPoint[] getScanResults(boolean combineBSSIDs) {
        return WpaCliPlugin.getScanResults(combineBSSIDs);
    }

    /**
     * Trampoline to internal listNetworks()
     * @see NetworkBackend$WpaCliPlugin.listNetworks()
     */
    public static SavedNetwork[] listNetworks() {
        return WpaCliPlugin.listNetworks();
    }

    /**
     * Get the current network status. If the command hasn't finished executing in the backend,
     * this function returns null.
     * 
     * @return the command response if finished or null.
     */
    public static HashMap<String, String> status() {
        if (EventThread.getJobQueued("wpa_cli_status"))
            return null;
        else if (EventThread.jobComplete("wpa_cli_status"))
            return EventThread.<HashMap<String, String>>getResponse("wpa_cli_status").getResponse();

        EventThread.queueJob(new EVTCommand<Void>("wpa_cli_status", null));
        return null;
    }

    /**
     * Trampoline to internal status()
     * @see NetworkBackend$WpaCliPlugin.status()
     */
    public static HashMap<String, String> status0() {
        return WpaCliPlugin.status();
    }

    /**
     * Set up an association to the provided network and save it to the
     * SavedNetworks list.
     * 
     * @param ap The selected AP
     * @param psk Pre-shared key, if necessary.
     * @return Whether the association was successful or not. Just because
     * this function returns true does not guarantee a successful connection/association
     */
    public static boolean associate(AccessPoint ap, String psk) {
        if (activeWlanEntry == ENTRY_NO_NETWORK)
            return false;

        // Create a new network entry for it
        int networkID = WpaCliPlugin.addNetwork();

        // Failed to create a network ID
        if (networkID == -1) {
            System.err.println("failed to create network id");
            return false;
        }

        if (!WpaCliPlugin.setNetwork(networkID, WpaCliPlugin.SET_NET_SSID, sanitizeInput(ap.ssid))) {
            System.err.println("invalid ssid");
            return false;
        }

        // Authentication is really weird to determine.
        if (!setAuthMode(networkID, ap)) {
            System.err.println("invalid auth mode");
            return false;
        }

        // Apparently using set psk is fine for both SAE and PSK
        if (!WpaCliPlugin.setNetwork(networkID, WpaCliPlugin.SET_NET_PSK, sanitizeInput(psk))) {
            System.err.println("invalid psk");
            return false;
        }

        // Need to save the config (Doesn't work on windows)
        if (!WpaCliPlugin.saveConfig()) {
            System.err.println("failed to save config; not fatal");
            return true;
        }

        return true;
    }

    /**
     * Set the network authentication mode as determined by the network flags.
     * 
     * @param networkID The ID of this network.
     * @param ap The access point.
     * @return Whether the auth set was successful.
     */
    private static boolean setAuthMode(int networkID, AccessPoint ap) {
        // Residential; probably using WPA-PSK (TKIP/CCMP; can't tell which).
        if (ap.getFlagsSet(AccessPoint.FLAG_WPA_PSK | AccessPoint.FLAG_WPA2_PSK))
            return WpaCliPlugin.setNetwork(networkID, WpaCliPlugin.SET_NET_KEY_MGMT, WpaCliPlugin.KEY_MGMT_WPA_PSK);

        else if (ap.getFlagsSet(AccessPoint.FLAG_WPA3_SAE))
            return WpaCliPlugin.setNetwork(networkID, WpaCliPlugin.SET_NET_KEY_MGMT, WpaCliPlugin.KEY_MGMT_SAE);

        // Probably an open authentication network.
        else if (ap.areOnlyFlagsSet(AccessPoint.FLAG_ESS))
            return WpaCliPlugin.setNetwork(networkID, WpaCliPlugin.SET_NET_KEY_MGMT, WpaCliPlugin.KEY_MGMT_OPEN);

        System.err.println("Unable to determine authentication mode for network " + ap.ssid + " w/ flags " + ap.flags);      
        return false;
    }
    
    /**
     * Reconnect to a saved network. Also performs the DHCP handshake.
     * 
     * @param network The network to use
     * @return Whether the connection worked.
     */
    public static boolean reconnect(SavedNetwork network) {
        if (activeWlanEntry == ENTRY_NO_NETWORK)
            return false;

        if (!WpaCliPlugin.networkOperation(WpaCliPlugin.OP_SELECT_NET, network.networkID))
            return false;

        // Need to save the config (Doesn't work on windows)
        if (!WpaCliPlugin.saveConfig()) {
            System.err.println("failed to save config; not fatal");
            return true;
        }

        return true;
    }

    static void rundhcpcd() {
        // Automatically handle obtaining an IP address
        System.out.println("pushing dhcp job");

        SMCallbackFunction cb = new SMCallbackFunction() {
            public void run() throws SMStopExecution {
                System.out.println("running dhcpcd...");

                CommandUtils.executeCommandRetry("sudo", "dhcpcd", "-x");
                CommandOutput output = CommandUtils.executeCommandRetry("sudo", "dhcpcd");

                if (output.getExitCode() == 0) {
                    System.out.println("network associated; got ip");
                    runWaitForNetwork();
                    throw new SMStopExecution();
                }

                System.out.println("failed to determine ip; retrying in 5s");
            }
        };

        if (Thread.currentThread().getName().equals("main"))
            SystemManagementThread.repeatingJob(5, cb);
        else
            SystemManagementThread.repeatingJobDeferred(5, cb);
    }

    /**
     * Starts the dhcpcd assoc thread after network is up and online.
     */
    public static void runWaitForNetwork() {
        System.out.println("pushing wait for network");

        HashMap<String, String> flags = status0();
        boolean initialLinkState = isConnected0(flags) && hasIP0(flags);

        SMCallbackFunction cb = new SMCallbackFunction() {
            boolean modeLinkUp = initialLinkState;

            public void run() {            
                if (modeLinkUp)
                    runLinkUp();
                else
                    runLinkDown();
            }

            /**
             * Standard mode. Waits for the link to come online then starts the dhcp
             * service.
             */
            private void runLinkDown() {
                // Request an IP address.
                if (isConnected()) {
                    System.out.println("link up, starting dhcp service");
                    rundhcpcd();
                    throw new SMStopExecution();
                }

                // Network isn't up yet.... Do nothing
            }

            /**
             * Standby mode. Waits for the link to go down, then switches to link down
             * mode.
             */
            private void runLinkUp() {
                if (!isConnected()) {
                    System.out.println("link down, entering wait mode");
                    this.modeLinkUp = false;
                }
            }
        };

        // Sometimes called on the main thread so avoid deadlocking when
        // re-calling.
        if (Thread.currentThread().getName().equals("main"))
            SystemManagementThread.repeatingJob(3, cb);
        else
            SystemManagementThread.repeatingJobDeferred(3, cb);
    }

    public static boolean isConnected() {
        return isConnected0(status0());
    }
    
    public static boolean isConnected0(HashMap<String, String> flags) {
        return flags.get("wpa_state").equals("COMPLETED");
    }

    public static boolean hasIP() {
        return hasIP0(status0());
    }

    public static boolean hasIP0(HashMap<String, String> flags) {
        return flags.get("ip_address") != null;
    }

    /**
     * Permanently remove a network from the list of saved networks.
     * 
     * @param network The network to forget.
     * @return Whether the network was successfully forgotten or not.
     */
    public static boolean forget(SavedNetwork network) {
        return WpaCliPlugin.networkOperation(WpaCliPlugin.OP_REMOVE_NET, network.networkID);
    }

    public static boolean enableNetwork(SavedNetwork network) {
        return WpaCliPlugin.networkOperation(WpaCliPlugin.OP_ENABLE_NET, network.networkID);
    }

    public static boolean disableNetwork(SavedNetwork network) {
        return WpaCliPlugin.networkOperation(WpaCliPlugin.OP_DISABLE_NET, network.networkID);
    }

    /**
     * Here because hackers like to poke around. Forbid any weird looking code sequences
     * and parse the input text to ensure it's safe to pass through to the command engine.
     * 
     * @param input Input to sanitize.
     * @return Input without the dangerous characters (if possible).
     */
    private static String sanitizeInput(String input) {
        // sanitize everything (may or may not be reliable; not tested yet)
        input = input.replace("\\", "\\\\")
                     .replace("\b", "\\b")
                     .replace("\n", "\\n") 
                     .replace("\r", "\\r")
                     .replace("\f", "\\f")
                     .replace("\"", "\\\"");

        // NOTE: Windows wants escaped quotes passed in directly.
        // i.e. set_network 0 ssid \"foo bar\" (as seen by the kernel)
        return String.format("\\\"%s\\\"", input);
    }

    /**
     * Contains an internal implementation of the wpa_cli interface.
     * Required for basic networking functionality.
     */
    private static class WpaCliPlugin {
        static final String OP_SELECT_NET           = "select_network";
        static final String OP_ENABLE_NET           = "enable_network";
        static final String OP_DISABLE_NET          = "disable_network";
        static final String OP_REMOVE_NET           = "remove_network";

        static final String SET_NET_SSID            = "ssid";
        static final String SET_NET_KEY_MGMT        = "key_mgmt";
        static final String SET_NET_PSK             = "psk";

        static final String KEY_MGMT_OPEN           = "NONE";
        static final String KEY_MGMT_WPA_PSK        = "WPA-PSK";
        //static final String KEY_MGMT_WPA_PSK_SHA256 = "WPA-PSK-SHA256";
        static final String KEY_MGMT_SAE            = "SAE";

        /**
         * Backend function for initiating an AP scan.
         * @apiNote Exported to the frontend.
         * 
         * wpa_cli scan
         *  Selected interface 'wlanX'
         *  OK <--- We want this (if it says FAIL then we have a problem)
         * 
         * @return Whether the scan could be started or not.
         */
        static boolean startScan() {
            if (activeWlanEntry == ENTRY_NO_NETWORK)
                return false;

            CommandOutput output = CommandUtils.executeCommandRetry("wpa_cli", "scan");
            return commandOkay_wpa_cli(output);
        }

        /**
         * Get the results of the previous scan attempt. Continually updated
         * so it's a good idea to call this function every few seconds or so.
         * @apiNote Exported to the frontend.
         * 
         *  wpa_cli scan_results
         *  Selected interface 'wlanX'
         *  bssid / frequency / signal level / flags / ssid
         *  02:00:00:00:01:00     2412      -30       [WPA-PSK-TKIP][ESS]    test
         *  (seems like each parameter is tab separated)
         * 
         * @param combineBSSIDs Combine each network with a different BSSID into
         *  the same entry.
         * @return A list of scanned access points. If BSSIDs are combined, the closest
         *  BSSID will be provided in the network entry.
         */
        static AccessPoint[] getScanResults(boolean combineBSSIDs) {
            if (activeWlanEntry == ENTRY_NO_NETWORK)
                return new AccessPoint[0];

            CommandOutput output = CommandUtils.executeCommandRetry("wpa_cli", "scan_results");
            checkExitCode_wpa_cli(output);

            String[] rawLines = output.getStdout().split("\n");

            // Skip the "selected interface" text (could be disastrous later.)
            int startLineOffset = 0;

            if (isWpaCliBanner(rawLines[0]))
                startLineOffset++;

            // wpa_cli always has this banner (that for whatever reason isn't tab aligned (??!)).
            if (!rawLines[startLineOffset].strip().equals("bssid / frequency / signal level / flags / ssid")) {
                System.err.println("Got unexpected banner");
                System.err.println(output.getStdout());
                return new AccessPoint[0];
            }

            // Everything past the banner is networks.
            AccessPoint[] discoveredNetworks = new AccessPoint[rawLines.length - 1];

            for (int i = startLineOffset + 1; i < rawLines.length; i++) {
                // Each field appears to be tab delimited?
                String[] components = rawLines[i].split("\t");

                discoveredNetworks[i - startLineOffset - 1] = new AccessPoint(
                    components[0], // bssid 
                    components[4], // ssid
                    Integer.parseInt(components[1]), // frequency 
                    Integer.parseInt(components[2]), // rssi
                    components[3] // flags, parsed by the AccessPoint logic
                );
            }

            if (!combineBSSIDs)
                return discoveredNetworks;

            // Deduplicate based on BSSIDs (they take up space and honestly it doesn't really
            // matter much for this use case).
            HashSet<String> ssids = new HashSet<>();
            ArrayList<AccessPoint> outNetworks = new ArrayList<>();

            for (AccessPoint ap : discoveredNetworks) {
                if (ap == null)
                    continue; // TODO: FIX (should not be null)

                if (ssids.contains(ap.ssid))
                    continue;

                ssids.add(ap.ssid);
                outNetworks.add(ap);
            }

            return outNetworks.toArray(AccessPoint[]::new);
        }

        /**
         * List the currently registered networks (networks that have been configured
         * already).
         * @apiNote Exported to the frontend.
         * 
         * wpa_cli list_networks
         *  Selected interface 'wlanX'
         *  network id / ssid / bssid / flags
         *  0           any     [DISABLED]
         *  1   test    any     [CURRENT]
         *  (parameters are tab delimited)
         *
         *  Flags:
         *      [DISABLED]: Network connection is not active.
         *      [TEMP-DISABLED]: Connection disabled due to authentication failures.
         *      [CURRENT]: Current connected network.
         */
        static SavedNetwork[] listNetworks() {
            if (activeWlanEntry == ENTRY_NO_NETWORK)
                return new SavedNetwork[0];

            CommandOutput output = CommandUtils.executeCommandRetry("wpa_cli", "list_networks");
            checkExitCode_wpa_cli(output);

            String[] rawLines = output.getStdout().split("\n");
            int startLineOffset = 0;

            if (isWpaCliBanner(rawLines[0]))
                startLineOffset++;

            // wpa_cli always has this banner (that for whatever reason isn't tab aligned (??!)).
            if (!rawLines[startLineOffset].strip().equals("network id / ssid / bssid / flags")) {
                System.err.println("Got unexpected banner");
                System.err.println(output.getStdout());
                return new SavedNetwork[0];
            }

            // Everything past the banner is networks.
            SavedNetwork[] networks = new SavedNetwork[rawLines.length - 1];

            for (int i = startLineOffset + 1; i < rawLines.length; i++) {
                // Each field appears to be tab delimited?
                String[] components = rawLines[i].split("\t");

                networks[i - 1] = new SavedNetwork(
                    Integer.parseInt(components[0]), // id 
                    components[1], // ssid 
                    components[2], // bssid
                    components.length != 3 ? components[3] : "" // state
                );
            }

            return networks;
        }

        /**
         * Get the current interface status.
         * Only public facing since no wrapper is necessary for security or ease of use.
         * @apiNote Exported to the frontend.
         * 
         * wpa_cli status
         *  Selected interface 'wlanX'
         *  bssid=02:00:00:00:01:00
         *  freq=2412
         *  id=0
         *  ... (ignoring irrelevant fields)
         *  key_mgmt=WPA-PSK
         *  wpa_state=COMPLETED (INACTIVE for disabled network, SCANNING for active scan, DISCONNECTED for no connection, 
         *      ASSOCIATING for connecting)
         *  address=02:00:00:00:00:00
         * 
         * @return A key/value list with the current network parameters. Before any other parameters
         *  are parsed it's recommended to check wpa_state, since not all parameters are always available.
         */
        static HashMap<String, String> status() {
            if (activeWlanEntry == ENTRY_NO_NETWORK) {
                HashMap<String, String> defaultOutput = new HashMap<>();
                defaultOutput.put("wpa_state", "NO_CARD_PRESENT");
                return defaultOutput;
            }

            CommandOutput output = CommandUtils.executeCommandRetry("wpa_cli", "status");
            checkExitCode_wpa_cli(output);

            String[] lines = output.getStdout().split("\n");
            int startLineOffset = 0;

            if (isWpaCliBanner(lines[0]))
                startLineOffset++;

            HashMap<String, String> nicStatus = new HashMap<>();

            for (int i = startLineOffset; i < lines.length; i++) {
                String[] keyValue = lines[i].split("=");
                nicStatus.put(keyValue[0].strip(), keyValue[1].strip());
            }

            return nicStatus;
        }

        /**
         * Register a new network type with the backend.
         * 
         * wpa_cli add_network
         *  Selected interface 'wlanX'
         *  0 <-- we want this network ID
         * 
         * @return A new network ID.
         */
        static int addNetwork() {
            if (activeWlanEntry == ENTRY_NO_NETWORK)
                return -1;

            CommandOutput output = CommandUtils.executeCommandRetry("wpa_cli", "add_network");
            checkExitCode_wpa_cli(output);

            // Find the network ID.
            String[] lines = output.getStdout().split("\n");
            return Integer.parseInt((isWpaCliBanner(lines[0]) ? lines[1] : lines[0]).strip());
        }

        /**
         * Utility function to lump some single operand network functions together 
         * (logic is nearly identical for each.)
         * 
         * wpa_cli select_network 0
         *  Selected interface 'wlanX'
         *  OK
         *
         * wpa_cli disable_network 0
         *  Selected interface 'wlanX'
         *  OK
         * 
         * wpa_cli enable_network 0
         *  Selected interface 'wlanX'
         *  OK
         * 
         * wpa_cli remove_network 0
         *  Selected interface 'wlanX'
         *  OK
         * 
         * @param operation The wpa_cli subcommand to execute.
         * @param id The network id.
         * @return Whether the command completed successfully.
         */
        static boolean networkOperation(String operation, int id) {
            if (activeWlanEntry == ENTRY_NO_NETWORK)
                return false;

            CommandOutput output = CommandUtils.executeCommandRetry("wpa_cli", operation, "" + id);
            return commandOkay_wpa_cli(output);
        }

        /**
         * Set a parameter for a specific network type.
         * Should be called by a wrapper function.
         * 
         * wpa_cli set_network 0 ssid '"test"'
         *  Selected interface 'wlanX'
         *  OK <-- fails sometimes idk why
         * 
         * wpa_cli set_network 0 key_mgmt <type>
         *  Selected interface 'wlanX'
         *  OK <-- should work usually
         * 
         *  parameters <type>:
         *      NONE: WPA is not used; plaintext or static WEP could be used
         *      WPA-PSK: WPA pre-shared key (this requires 'psk' field)
         *      WPA-EAP: WPA using EAP authentication
         *      WPA-PSK-SHA256: Like WPA-PSK but using stronger SHA256-based algorithms
         *      WPA-EAP-SHA256: Like WPA-EAP but using stronger SHA256-based algorithms
         * 
         * wpa_cli set_network 0 psk '"12345678"'
         *  Selected interface 'wlanX'
         *  OK <-- Should work....
         * 
         * @param id Network ID
         * @param key The network parameter key to set.
         * @param value The value of the key.
         * @return whether the set was successful.
         */
        static boolean setNetwork(int id, String key, String value) {
            if (activeWlanEntry == ENTRY_NO_NETWORK)
                return false;

            CommandOutput output = CommandUtils.executeCommandRetry(
                "wpa_cli", 
                "set_network", 
                "" + id, 
                key, 
                value
            );

            return commandOkay_wpa_cli(output);
        }

        /**
         * Save the current network configuration.
         * 
         * wpa_cli save_config
         *  Selected interface 'wlanX'
         *  OK <-- ...
         * 
         * @return if the config was saved successfully.
         */
        static boolean saveConfig() {
            if (activeWlanEntry == ENTRY_NO_NETWORK)
                return false;

            CommandOutput output = CommandUtils.executeCommandRetry("wpa_cli", "save_config");
            return commandOkay_wpa_cli(output);
        }

        /**
         * Most wpa_cli commands list the interface, then the command status.
         * First line should have some variant of "Selected interface 'wlanX'".
         * Second line will say okay or fail.
         * 
         * @param output
         * @return
         */
        private static boolean commandOkay_wpa_cli(CommandOutput output) {
            checkExitCode_wpa_cli(output);

            String[] lines = output.getStdout().split("\n");

            // wpa_cli commands have a "uniquely" identifiable marker at the top usually.
            if (isWpaCliBanner(lines[0]))
                return lines[1].strip().equals("OK");

            // No banner; just read the output.
            return lines[0].strip().equals("OK");
        }

        /**
         * Determine the existence of the "Selected interface banner"
         * to reduce possible breakages.
         * 
         * @param line Suspected line
         * @return Whether it is the banner.
         */
        private static boolean isWpaCliBanner(String line) {
            //System.out.println("found banner " + line.strip().matches("Selected interface '.*'"));
            return line.strip().matches("Selected interface '.*'");
        }

        /**
         * Use when you don't care about the text output, but just the exit code.
         * Otherwise, to check command execution, use the above function.
         * 
         * @param output Command results.
         */
        private static void checkExitCode_wpa_cli(CommandOutput output) {
            if (output == null) {
                System.err.println("======================= UNEXPECTED ERROR IN WPA_CLI =======================");
                System.err.println("GOT NO RESPONSE FROM WPA_CLI/WPA_CLI NOT INSTALLED!");
                System.err.println("========================= END WPA_CLI ERROR REPORT ========================");

                throw new RuntimeException("Internal bugcheck case encountered in wpa_cli code. See above error.");
            }

            // Unknown command provided. Probably a bug in this code.
            if (output.getExitCode() != 0) {
                System.err.println("======================= UNEXPECTED ERROR IN WPA_CLI =======================");
                System.err.println("Got exit code " + output.getExitCode());
                System.err.println("================= captured stdout below ==v");
                System.err.println(output.getStdout());
                System.err.println("================= captured stderr below ==v");
                System.err.println(output.getStderr());
                System.err.println("========================= END WPA_CLI ERROR REPORT ========================");

                throw new RuntimeException("Internal bugcheck case encountered in wpa_cli code. See above error.");
            }
        }
    }
}
