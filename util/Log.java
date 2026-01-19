package util;

import system.Config;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Log {
    public static enum LogLevel {
        FATAL,
        ERROR,
        WARN,
        INFO,
        VERBOSE
    }

    static final FileWriter logWriter;
    static final long startTime;
    static LogLevel logLevel;

    static {
        logLevel = Config.INITIAL_LOG_LEVEL;

        File logPath = new File("~/.local/share/rsc-games/menu-logs");

        if (!logPath.exists()) {
            System.out.println("creating log directory");
            logPath.mkdirs();
        }

        File logFilePath = new File(logPath.getPath() + "/log-" + LocalDateTime.now() + ".log");
        System.out.println("opening log file " + logFilePath.getAbsolutePath());

        try {
            logWriter = new FileWriter(logFilePath);
            logWriter.append("==== STARTED LOG FILE WRITER ====\n");
        }
        catch (IOException ie) {
            System.err.println("FATAL: CANNOT CREATE LOG FILES!!!!");
            throw new RuntimeException(ie);
        }

        startTime = System.currentTimeMillis();
    }

    /**
     * Set the active log level.
     * 
     * @param level New active log level.
     */
    public static void setLogLevel(LogLevel level) {
        logLevel = level;
    }

    /**
     * Log a message at the verbose level, if that level is currently active.
     * 
     * @param message Verbose message to log.
     */
    public static synchronized void logVerbose(String message) {
        if (logLevel.compareTo(LogLevel.VERBOSE) < 0)
            return;

        // Format it blue.
        message = String.format("\033[34mV [%d]: %s\033[0m\n", getMillis(), message);
        System.out.print(message);

        try {
            logWriter.append(message);
        }
        catch (IOException ie) {
            System.err.println("Warning: failed to write message to logfile!");
        }
    }

    /**
     * Log a message at the information level, if that level is currently active.
     * 
     * @param message info message to log.
     */
    public static synchronized void logInfo(String message) {
        if (logLevel.compareTo(LogLevel.INFO) < 0)
            return;

        // Color it green.
        message = String.format("\033[32mI [%d]: %s\033[0m\n", getMillis(), message);
        System.out.print(message);

        try {
            logWriter.append(message);
        }
        catch (IOException ie) {
            System.err.println("Warning: failed to write message to logfile!");
        }
    }

    /**
     * Log a message at the warning level, if that level is currently active.
     * 
     * @param message warning to log.
     */
    public static synchronized void logWarning(String message) {
        if (logLevel.compareTo(LogLevel.WARN) < 0)
            return;

        // Color it yellow.
        message = String.format("\033[33mW [%d]: %s\033[0m\n", getMillis(), message);
        System.err.print(message);

        try {
            logWriter.append(message);
        }
        catch (IOException ie) {
            System.err.println("Warning: failed to write message to logfile!");
        }
    }

    /**
     * Log a message at the error level, if that level is currently active.
     * 
     * @param message error message to log.
     */
    public static synchronized void logError(String message) {
        if (logLevel.compareTo(LogLevel.WARN) < 0)
            return;

        // Color it red.
        message = String.format("\033[31mE [%d]: %s\033[0m\n", getMillis(), message);
        System.err.print(message);

        try {
            logWriter.append(message);
        }
        catch (IOException ie) {
            System.err.println("Warning: failed to write message to logfile!");
        }
    }

    /**
     * Log a message at the fatal level, if that level is currently active.
     * 
     * @param message fatal error message to log.
     */
    public static synchronized void logFatal(String message) {
        // Color it red.
        message = String.format("\033[31;103mFATAL ERROR [%d]: %s\033[0m\n", getMillis(), message);
        System.err.print(message);

        try {
            logWriter.append(message);
        }
        catch (IOException ie) {
            System.err.println("Warning: failed to write message to logfile!");
        }
    }

    public static synchronized void logException(Exception ie) {
        try {
            logWriter.append("\n============ UNEXPECTED RUNTIME EXCEPTION CAUGHT ================\n");

            // Format the stack trace for displaying on screen.
            StackTraceElement[] elements = ie.getStackTrace();
            String[] exceptionLines = new String[elements.length + 1];
            exceptionLines[0] = "Exception in thread " + Thread.currentThread().getName() 
                                + " " + ie.getClass().getName() + " " + ie.getMessage();

            for (int i = 0; i < elements.length; i++)
                exceptionLines[i + 1] = "      at " + elements[i].toString();

            for (String line : exceptionLines)
                logWriter.append(line + "\n");

            logWriter.append("\n===================== End of backtrace =========================\n");
        }
        catch (IOException exc) {
            System.err.println("Warning: failed to write backtrace to logfile!");
        }
    }

    static long getMillis() {
        return System.currentTimeMillis() - startTime;
    }
}
