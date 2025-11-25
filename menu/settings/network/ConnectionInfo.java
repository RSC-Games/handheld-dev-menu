package menu.settings.network;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;

import backend.NetworkBackend;
import ui.UIElement;
import ui.UIKeyValueText;
import ui.UIText;

class ConnectionInfo extends UIText {
    // Information is refreshed every 2 seconds.
    private static final int REFRESH_FRAMES = 120;
    int counter = 0;

    /**
     * Text area is rendered like this
     * 
     * WLAN Connection Details:
     *      status                          ssid
     *      mac_address                     frequency
     *      network_id                      ip_address
     *                                      auth
     *                              
     */
    UIKeyValueText wlanStatus;
    UIKeyValueText wlanMACAddress;
    UIKeyValueText networkIDBox;
    UIKeyValueText ssidBox;
    UIKeyValueText freqBox;
    UIKeyValueText ipAddressBox;
    UIKeyValueText authBox;

    public ConnectionInfo(UIElement parent, Point loc) {
        super(parent, loc, "WLAN Connection Details: ", Color.white, 12);
        this.wlanStatus = new UIKeyValueText(this, new Point(25, 18), Color.white);
        this.wlanMACAddress = new UIKeyValueText(this, new Point(25, 30), Color.white);
        this.networkIDBox = new UIKeyValueText(this, new Point(25, 42), Color.white);
        this.ssidBox = new UIKeyValueText(this, new Point(400, 18), Color.white);
        this.freqBox = new UIKeyValueText(this, new Point(400, 30), Color.white);
        this.ipAddressBox = new UIKeyValueText(this, new Point(400, 42), Color.white);
        this.authBox = new UIKeyValueText(this, new Point(400, 54), Color.white);

        initializeFields();
    }

    private void initializeFields() {
        this.wlanStatus.setKeyText("status");
        this.wlanMACAddress.setKeyText("mac_addr");
        this.ipAddressBox.setKeyText("ipv4_addr");
        this.networkIDBox.setKeyText("network_id");
        this.ssidBox.setKeyText("ssid");
        this.freqBox.setKeyText("frequency");
        this.authBox.setKeyText("auth");
    }
    
    protected void tick() {
        if (counter-- >= 0)
            return;

        // Avoid nasty crash when the interface isn't accessible
        if (!NetworkBackend.wlanEnabled()) {
            this.wlanStatus.setValueText("BLOCKED");
            this.wlanMACAddress.setValueText("<can't read>");
            setStatusOff();
            return;
        }

        HashMap<String, String> statusFlags = NetworkBackend.status();

        // Hasn't finished processing the command yet.
        if (statusFlags == null)
            return;

        String status = statusFlags.get("wpa_state");

        if (status == null) {
            this.wlanStatus.setValueText("ERROR");
            return;
        }

        // Status can be set regardless of whether it's active or not.
        this.wlanStatus.setValueText(status);
        this.wlanMACAddress.setValueText(statusFlags.get("address"));

        // Display relevant network state info
        if (!status.equals("INACTIVE") && !status.equals("DISCONNECTED")) {
            if (status.equals("COMPLETED"))
                this.wlanStatus.setValueColor(Color.green);
            else if (status.equals("ASSOCIATING") || status.equals("SCANNING"))
                this.wlanStatus.setValueColor(Color.orange);

            this.networkIDBox.setValueText(statusFlags.get("id"));
            this.ssidBox.setValueText(statusFlags.get("ssid"));
            this.freqBox.setValueText(statusFlags.getOrDefault("freq", "0") + " MHz");
            this.ipAddressBox.setValueText(statusFlags.getOrDefault("ip_address", "<unassigned>"));
            this.authBox.setValueText(statusFlags.getOrDefault("key_mgmt", "<determining>"));
        }
        else
            setStatusOff();

        counter = REFRESH_FRAMES;
    }

    private void setStatusOff() {
        this.wlanStatus.setValueColor(Color.red);

        this.networkIDBox.setValueText("");
        this.ssidBox.setValueText("<none>");
        this.freqBox.setValueText("0 MHz");
        this.ipAddressBox.setValueText("<unassigned>");
        this.authBox.setValueText("");
    }
}
