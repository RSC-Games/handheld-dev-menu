package menu.overlays.quick_settings;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;

import backend.NetworkBackend;
import ui.UIBase;
import ui.UIElement;
import ui.UIKeyValueText;
import ui.UIText;

class MiniConnectionInfo extends UIBase {
    // Information is refreshed every 2 seconds.
    private static final int REFRESH_FRAMES = 120;
    int counter = 0;

    /**
     * Text area is rendered like this
     * 
     * WiFi: connected    - 192.168.1.1
     * 
     */
    UIKeyValueText wlanStatus;
    UIText ipAddressBox;

    public MiniConnectionInfo(UIElement parent, Point loc) {
        super(parent, loc);
        this.wlanStatus = new UIKeyValueText(this, new Point(0, 0), Color.white);
        this.ipAddressBox = new UIText(this, new Point(125, 0), "- <no ip>", Color.white, 12);

        initializeFields();
    }

    private void initializeFields() {
        this.wlanStatus.setKeyText("WiFi");
        this.ipAddressBox.setText("- <no ip>");
    }
    
    protected void tick() {
        if (counter-- >= 0)
            return;

        // Avoid nasty crash when the interface isn't accessible
        if (!NetworkBackend.wlanEnabled()) {
            this.wlanStatus.setValueText("BLOCKED");
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
        this.wlanStatus.setValueText(status);;

        // Display relevant network state info
        if (!status.equals("INACTIVE") && !status.equals("DISCONNECTED")) {
            if (status.equals("COMPLETED"))
                this.wlanStatus.setValueColor(Color.green);
            else if (status.equals("ASSOCIATING") || status.equals("SCANNING"))
                this.wlanStatus.setValueColor(Color.orange);

            this.ipAddressBox.setText("- " + statusFlags.getOrDefault("ip_address", "<no ip>"));
        }
        else
            setStatusOff();

        counter = REFRESH_FRAMES;
    }

    private void setStatusOff() {
        this.wlanStatus.setValueColor(Color.red);
        this.ipAddressBox.setText("- <no ip>");
    }
}
