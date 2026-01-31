package backend;

import backend.system_mgmt.SMCallbackFunction;
import backend.system_mgmt.SystemManagementThread;
import util.Log;

public class BacklightManagement implements SMCallbackFunction {
    private static final BacklightManagement theService;

    static {
        theService = new BacklightManagement();
    }

    private int dimTimeoutMinutes = 1;
    private boolean dimmed = false;
    private int sleepTimeoutMinutes = 5;
    private boolean screenOff = false;

    private long idleStartMs = System.currentTimeMillis();

    // TODO: Need to read parameter store
    BacklightManagement() {
        Log.logInfo("backlight.service: pushing repeating job");
        SystemManagementThread.repeatingJob(5, this);
    }

    public static BacklightManagement getBacklightService() {
        return theService;
    }

    public void setDimTimeout(int minutes) {
        Log.logInfo("backlight.service: set dim timeout to " + minutes + " min");
        this.dimTimeoutMinutes = minutes;
    }

    public void setSleepTimeout(int minutes) {
        Log.logInfo("backlight.service: set sleep timeout to " + minutes + " min");
        this.sleepTimeoutMinutes = minutes;
    }

    public int getDimTimeout() {
        return this.dimTimeoutMinutes;
    }

    public int getSleepTimeout() {
        return this.sleepTimeoutMinutes;
    }

    /**
     * Reset all idle timers (user may have bumped a controller or keyboard)
     * Also powers the screen back on and undims it if necessary.
     */
    public void resetIdleTimer() {
        idleStartMs = System.currentTimeMillis();

        if (screenOff) {
            Log.logInfo("backlight.service: not idle; powering on screen");
            BacklightService.setPowerState(true);
            screenOff = false;
        }

        if (dimmed) {
            Log.logInfo("backlight.service: not idle; undimming screen");
            BacklightService.setDimState(false);
            dimmed = false;
        }
    }

    @Override
    public void run() {
        // Time reported in ms but we want minutes.
        long timeDeltaS = (System.currentTimeMillis() - idleStartMs) / 60000;

        if (timeDeltaS >= dimTimeoutMinutes && !dimmed) {
            Log.logInfo("backlight.service: timeout expired; dimming screen");
            BacklightService.setDimState(true);
            dimmed = true;
        }

        if (timeDeltaS >= sleepTimeoutMinutes && !screenOff) {
            Log.logInfo("backlight.service: timeout expired; entering sleep mode");
            BacklightService.setPowerState(false);
            screenOff = true;

            // TODO: Implement sleep mode backend.
        }
    }
}
