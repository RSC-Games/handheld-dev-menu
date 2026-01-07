package backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import backend.title.TitleInfo;
import util.Log;
import util.Utils;

/**
 * Utility for launching and maintaining a running title.
 */
public class TitleLaunchService {
    private static ArrayList<HideWindowCallback> callbacks = new ArrayList<>();
    private static RunningTitle currentTitle = null;

    /**
     * Wait on the running game thread until it terminates or an event
     * forces an unlock.
     */
    private static volatile boolean shouldHide = false;
    private static int hideTimer = -1;

    /**
     * Get the full list of titles from the title config file.
     * 
     * @param file The title config file
     * @return The parsed list of titles
     */
    public static TitleInfo[] getTitleList(File file) {
        try {
            String raw = Files.readString(file.toPath());
            String[] lines = raw.split("\n");

            TitleInfo[] res = new TitleInfo[lines.length];

            for (int i = 0; i < lines.length; i++)
                res[i] = new TitleInfo(lines[i]);

            return res;
        }
        catch (IOException ie) {
            System.err.println("Couldn't find titledb list " + file.getPath());
            return new TitleInfo[0];
        }
    }

    /**
     * Launch a title.
     * 
     * @param info The title to launch
     * @return Whether it was successfully launched or not.
     */
    public static boolean launchTitle(TitleInfo info) {
        if (currentTitle != null) {
            System.err.println("Tried to start process when one was already running!");
            return false;
        }

        currentTitle = new RunningTitle(info);
        boolean started = currentTitle.startProcess();
        Utils.sleepms(1);
        boolean launchSuccessful = started && currentTitle.isAlive();

        // Auto-reap.
        if (!launchSuccessful) {
            currentTitle = null;
            return false;
        }

        return true;
    }

    /**
     * Determine if the current title is still running.
     * 
     * @return True if it's still running, or false if it closed.
     */
    public static boolean isTitleRunning() {
        return currentTitle != null && currentTitle.isAlive();
    }

    /**
     * Determine if the provided title id is the current one running.
     * 
     * @param info The title in question
     * @return True if it's the one running, false otherwise.
     */
    public static boolean thisTitleActive(TitleInfo info) {
        return currentTitle != null && currentTitle.thisTitle.equals(info);
    }

    /**
     * Wait for an undeterminable time if the title is running.
     * Waits for an interrupt to re-run the menu system code.
     * 
     * @param enableWait whether the menu should sleep.
     */
    public static void setWindowHideState(boolean enableHide) {
        shouldHide = enableHide;
    }

    /**
     * Determine the current waiting state.
     * 
     * @return Whether the main thread should be waiting.
     */
    public static boolean getWindowHideState() {
        return shouldHide;
    }

    /**
     * Meant to be called by the application. If the application requests,
     * this will put the main thread back on hold before an interrupt or
     * if the app thread unexpectedly dies.
     */
    public static void poll() {
        if (hideTimer >= 0)
            hideTimer--;

        // Engage the auto-hide timer.
        if (hideTimer == 0) {
            setWindowHideState(true);

            for (HideWindowCallback cb : callbacks)
                cb.run();

            callbacks.clear();
        }

        // Refresh wait state
        if (currentTitle != null && !currentTitle.isAlive()) {
            setWindowHideState(false);
            // Dead, something else should reap it.
        }
    }

    /**
     * Automatically hide the window after this time frame.
     * 
     * @param seconds Time in seconds to hide it after.
     */
    public static void setHideTimer(int seconds) {
        hideTimer = seconds * 60;
    }

    /**
     * Register a callback to run when the window hide timer expires.
     * 
     * @param cb The callback to execute.
     */
    public static void registerWindowHideCallback(HideWindowCallback cb) {
        callbacks.add(cb);   
    }

    /**
     * Only reap a process if there is a dead process waiting.
     * 
     * @return If a process needs to be reaped.
     */
    public static boolean processWaitingForReap() {
        return currentTitle != null && !currentTitle.isAlive();
    }

    /**
     * Must be called and return to successfully reap the process.
     * 
     * @return The exit code.
     */
    public static int getExitCode() {
        int exitCode = currentTitle.wasTerminated() ? 0 : currentTitle.getExitCode();
        currentTitle = null;
        return exitCode;
    }

    /**
     * Terminate the title. Should cause an orderly shutdown of the application
     * and no data loss, but this isn't guaranteed.
     */
    public static void terminate() {
        currentTitle.closeApp();
    }


    private static class RunningTitle {
        private TitleInfo thisTitle;
        private Process executingThread;
        private boolean wasTerminated = false;

        RunningTitle(TitleInfo thisTitle) {
            this.thisTitle = thisTitle;
        }

        boolean startProcess() {
            ProcessBuilder titleToLaunch = new ProcessBuilder(thisTitle.args);
            Log.logInfo("title.service: running title launch command " + titleToLaunch.command());
            titleToLaunch.inheritIO();

            try {
                this.executingThread = titleToLaunch.start();
                return true;
            }
            catch (IOException ie) {
                System.err.println("failed to execute process args " + Arrays.toString(thisTitle.args));
                return false;
            }
        }

        boolean isAlive() {
            return executingThread != null && executingThread.isAlive();
        }

        /**
         * Assumed the process is already dead, so reap now.
         * 
         * @return The process exit code.
         */
        int getExitCode() {
            return executingThread.exitValue();
        }

        void closeApp() {
            // Kill all descendents too (avoid errant processes like mc)
            for (ProcessHandle child : executingThread.children().<ProcessHandle>toArray(ProcessHandle[]::new))
                child.destroy();

            executingThread.destroy();
            wasTerminated = true;
        }

        boolean wasTerminated() {
            return wasTerminated;
        }
    }
}
