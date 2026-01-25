package backend;

import backend.CommandUtils.CommandOutput;
import util.Log;

public class PerformanceMonitor {
    public static int getCPUUtilization() {
        // TODO: /proc/stats (cpu)
        return 0;//new Random().nextInt(0, 15);
    }

    public static int[] getPerCoreUtilization() {
        // TODO: /proc/stats (cpuX)
        return new int[4];
    }

    /**
     * Get the instantaneous ARM frequency at this time in MHz.
     * 
     * @return CPU clock frequency in MHz.
     */
    public static int getCPUFrequencyMHz() {
        return getDomainClockSpeed("arm");
    }

    public static int getGPUUtilization() {
        // TODO: read and parse /sys/devices/platform/axi/1002000000.v3d/gpu_stats
        return 0; //new Random().nextInt(0, 15);
    }

    /**
     * Get the instantaneous V3D frequency at this time in MHz.
     * 
     * @return GPU clock frequency in MHz.
     */
    public static int getGPUFrequencyMHz() {
        return getDomainClockSpeed("v3d");
    }

    /**
     * Get the current SoC V_core (applies to both CPU, GPU, and uncore)
     * 
     * @return Main SoC tile V_core
     */
    public static float getSoCVcore() {
        CommandOutput output = CommandUtils.executeCommandRetry("vcgencmd", "measure_volts");

        if (output.getExitCode() != 0) {
            Log.logError("perfmon: failed to read SoC V_core");
            Log.logVerbose("stdout: " + output.getStdout());
            Log.logVerbose("stderr: " + output.getStderr());
            return 0.5f; // Idling RPi 4 V_core is 0.675 V; anything lower is obviously wrong.
        }

        String stdout = output.getStdout();
        int startidx = stdout.indexOf("=");
        int endidx = stdout.indexOf("V");

        // Filter out the leading volt= and the V at the end.
        float vcore = Float.parseFloat(stdout.substring(startidx + 1, endidx).strip());
        return vcore;
    }

    /**
     * Get the temperature of the entire SoC (assuming averaged thermal reading?)
     * 
     * @return Main SoC tile temperature.
     */
    public static int getSoCTemperature() {
        CommandOutput output = CommandUtils.executeCommandRetry("vcgencmd", "measure_temp");

        if (output.getExitCode() != 0) {
            Log.logError("perfmon: failed to read SoC V_core");
            Log.logVerbose("stdout: " + output.getStdout());
            Log.logVerbose("stderr: " + output.getStderr());
            return 0; // Unlikely the thing is going to still be at freezing after boot, even in cold weather.
        }

        String stdout = output.getStdout();
        int startidx = stdout.indexOf("=");
        int endidx = stdout.indexOf(".");

        // Filter out the leading temp= and the .x'C at the end.
        int temp = Integer.parseInt(stdout.substring(startidx + 1, endidx).strip());
        return temp;
    }

    /**
     * Get the physical memory size in MB.
     * 
     * @return Physical memory size (in MB)
     */
    public static int getSDRAMTotalMB() {
        return getMemoryTypeMB("MemTotal");
    }

    /**
     * Get the available heap size in MB.
     * 
     * @return Available physical memory size (in MB)
     */
    public static int getSDRAMAvailableMB() {
        return getMemoryTypeMB("MemAvailable");
    }

    /**
     * Get the clock speed for a given hardware domain. Internally wraps vcgencmd.
     * 
     * @param domain The clock domain
     * @return the frequency in MHz
     */
    private static int getDomainClockSpeed(String domain) {
        CommandOutput output = CommandUtils.executeCommandRetry("vcgencmd", "measure_clock", domain);

        if (output.getExitCode() != 0) {
            Log.logError("perfmon: failed to read " + domain + " freq");
            Log.logVerbose("stdout: " + output.getStdout());
            Log.logVerbose("stderr: " + output.getStderr());
            return 50; // No clock domain will idle below 50 MHz; the Pi idles at 600 MHz/1.5 GHz (Pi 4/5)
        }

        long freq = Long.parseLong(output.getStdout().split("=")[1].strip());
        return (int)(freq / 1_000_000);
    }

    /**
     * Determine various Linux memory region sizes. All we want here is the physical memory
     * size but other stuff could be useful.
     * 
     * @param flag The memory region we want.
     * @return The memory region size in kB
     */
    private static int getMemoryTypeMB(String flag) {
        // The proper way to do this is with the native file I/O of java...
        // Or coreutils can do it for me since the logic is already here.
        // TODO: Use proper file I/O to read the file.
        CommandOutput output = CommandUtils.executeCommandRetry("cat", "/proc/meminfo");

        if (output.getExitCode() != 0) {
            Log.logError("perfmon: failed to read meminfo");
            Log.logVerbose("stdout: " + output.getStdout());
            Log.logVerbose("stderr: " + output.getStderr());
            return 0; // Can't read memory info? You must have NO RAM >:D
        }

        String[] memFlags = output.getStdout().split("\n");
        String requestedFlag = null;

        for (String memFlag : memFlags) {
            if (memFlag.contains(flag))
                requestedFlag = memFlag;
        }

        if (requestedFlag == null)
            throw new RuntimeException("unable to find flag " + flag + " in /proc/meminfo");

        long memFlagKB = Long.parseLong(requestedFlag.split("\s+")[1].strip());
        return (int)(memFlagKB / 1_024);
    }
}
