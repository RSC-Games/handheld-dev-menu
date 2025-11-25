package util;

public class Utils {
    public static void sleepms(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ie) {}
    }
}
