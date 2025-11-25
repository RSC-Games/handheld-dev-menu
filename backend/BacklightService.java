package backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Exposes the system backlight as a clean API for the menu system to use.
 * Converts the internal scaling from 45-255 (40 when dimmed) to 0-100%.
 * When the dim state is requested, the backlight percentage is decreased
 * 50% (capped at raw brightness 45 minimum).
 */
public class BacklightService {
    private static final String DEV_NAME = "10-0045";
    private static final String PATH = String.join("/", "/sys/class/backlight", DEV_NAME);
    private static final String BRIGHTNESS_FILE = String.join("/", PATH, "brightness");
    private static final String MAX_BRIGHTNESS_FILE = String.join("/", PATH, "max_brightness");

    private static final int BRIGHTNESS_OFF = 0;
    private static final int MIN_BRIGHTNESS = 45; // Hard-coded for this screen; difficult to determine w/o experimentation
    private static final int MIN_BRIGHTNESS_DIM = 40;
    private static final int MAX_BRIGHTNESS = Integer.parseInt(readString(MAX_BRIGHTNESS_FILE)); // Usually 255
    private static final float DIM_SCALE_FACTOR = 0.3f;

    /**
     * Current panel brightness, scaled from 1 to 100
     */
    private static int currentPanelBrightness = -1;

    static {
        currentPanelBrightness = getScaledBrightness();
    }

    /**
     * Set the panel brightness percentage. The underlying hardware stores the brightness
     * differenty and requires offset calculation, which is done in here.
     * 
     * @param percentage The relative percentage of the LCD from 1 to 100%.
     */
    public static void setScaledBrightness(int percentage) {
        int rawBrightness = Math.round(mapRange(1, 100, MIN_BRIGHTNESS, MAX_BRIGHTNESS, percentage));
        setRawBrightness(rawBrightness);
    }

    private static void setRawBrightness(int rawBrightness) {
        writeString(BRIGHTNESS_FILE, "" + rawBrightness);
    }

    /**
     * Get the brightness of the panel, rescaled from 1 to 100%.
     * 
     * @return The panel brightness
     */
    public static int getScaledBrightness() {
        int rawBrightness = getRawBrightness();
        System.out.println("Got raw brightness " + rawBrightness);
        return Math.round(mapRange(MIN_BRIGHTNESS, MAX_BRIGHTNESS, 1, 100, rawBrightness));
    }

    private static int getRawBrightness() {
        return Integer.parseInt(readString(BRIGHTNESS_FILE));
    }

    /**
     * Set the current dim state of the panel. By design, the panel will automatically dim
     * after a few minutes of inactivity.
     * 
     * @param shouldDim Whether the panel should be dimmed.
     */
    public static void setDimState(boolean shouldDim) {
        // The panel dimmed can get darker than the minimum allowed brightness, since nobody is viewing it.
        if (shouldDim) {
            currentPanelBrightness = getScaledBrightness();
            int rawBrightness = Math.round(Math.max(MIN_BRIGHTNESS_DIM, getRawBrightness() * DIM_SCALE_FACTOR));
            setRawBrightness(rawBrightness);
        }
        // No special handling here; just restore the last brightness value.
        else {
            setScaledBrightness(currentPanelBrightness);
        }
    }

    /**
     * Turn the panel off and back on.
     * 
     * @param enabled Whether the panel should be enabled or not.
     */
    public static void setPowerState(boolean enabled) {
        if (!enabled) {
            currentPanelBrightness = getScaledBrightness();
            setRawBrightness(BRIGHTNESS_OFF);
        }
        else
            setScaledBrightness(currentPanelBrightness);
    }

    private static void writeString(String filePath, String data) {
        try {
            File writeFile = new File(filePath);
            PrintWriter outStream = new PrintWriter(new FileOutputStream(writeFile));
            outStream.write(data);
            outStream.close();
        }
        catch (FileNotFoundException ie) {
            ie.printStackTrace();
            System.err.println("Failed to set file " + filePath + " data " + data);
        }
    }

    private static String readString(String filePath) {
        try {
            File readFile = new File(filePath);
            Scanner inStream = new Scanner(new FileInputStream(readFile));
            String result = inStream.next();
            inStream.close();

            return result;
        }
        catch (FileNotFoundException ie) {
            ie.printStackTrace();
            System.err.println("Failed to read file " + filePath);
            return "45";
        }
    }

    /**
     * Maps a value s from the range [a1, a2] and maps it into the range [b1, b2].
     * 
     * @param a1 Low bound of range a
     * @param a2 High bound of range a
     * @param b1 Low bound of range b
     * @param b2 High bound of range b
     * @param s Value in range a
     * @return S in relation to range b
     */
    private static float mapRange(float a1, float a2, float b1, float b2, float s){
		return b1 + ((s - a1)*(b2 - b1))/(a2 - a1);
	}
}
