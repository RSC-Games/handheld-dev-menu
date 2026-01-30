package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Utils {
    public static void sleepms(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ie) {}
    }

    /**
     * Read all bytes from a given text file and return it as a String.
     * 
     * @param path Path of the file to read from
     * @return The string representation of the read data, or null if the
     *  data can't be read.
     */
    public static String readTextFile(String path) {
        try {
            FileInputStream bytes = new FileInputStream(new File(path));
            String output = new String(bytes.readAllBytes(), StandardCharsets.UTF_8);
            bytes.close();

            return output;
        }
        catch (IOException ie) {
            // Caller is responsible for error checking and reporting.
            //Log.logError("utils: failed to read file " + path);
            return null;
        }
    }
}
