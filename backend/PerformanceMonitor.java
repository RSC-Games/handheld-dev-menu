package backend;

import backend.CommandUtils.CommandOutput;
import util.Log;
import util.Utils;

public class PerformanceMonitor {
    /**
     * Get the CPU utilization across all execution cores.
     * 
     * @return The all core CPU utilization.
     */
    public static int getCPUUtilization() {
        return computeUtilization(-1);
    }

    public static int[] getPerCoreUtilization() {
        // TODO: Can't implement because of delta tracking
        return new int[4];
    }

    // For delta tracking.
    private static long cpuPrevIdleTime = 0;
    private static long cpuPrevTotalTime = 0;

    /**
     * Calculate the utilization for a given core (or all cores).
     * 
     * @param coreID Core ID (-1 for all cores)
     * @return The utilization percentage for the given core(s)
     */
    // TODO: Deltas are updated every time this is called (potentially causing issues).
    // Per core can't be properly supported until deltas are tracked.
    private static int computeUtilization(int coreID) {
        // CPU counters are stored as follows:
        // cpuX user nice system idle iowait irq softirq steal guest guest_nice
        String stats = Utils.readTextFile("/proc/stat");

        if (stats == null) {
            Log.logError("perfmon: unable to read /proc/stat");
            return -1;
        }

        String[] statsList = stats.split("\n");
        String[] cpuInfo = statsList[coreID + 1].split("\s+");

        // All fields are required for utilization calculation.
        long user = Long.parseLong(cpuInfo[1].strip());
        long nice = Long.parseLong(cpuInfo[2].strip());
        long system = Long.parseLong(cpuInfo[3].strip());
        long idle = Long.parseLong(cpuInfo[4].strip());
        long iowait = Long.parseLong(cpuInfo[5].strip());
        long irq = Long.parseLong(cpuInfo[6].strip());
        long softirq = Long.parseLong(cpuInfo[7].strip());
        long steal = Long.parseLong(cpuInfo[8].strip());
        long guest = Long.parseLong(cpuInfo[9].strip());
        long guest_nice = Long.parseLong(cpuInfo[10].strip());

        // PrevIdle = previdle + previowait
        // Idle = idle + iowait
        idle = idle + iowait;

        // PrevNonIdle = prevuser + prevnice + prevsystem + previrq + prevsoftirq + prevsteal
        // NonIdle = user + nice + system + irq + softirq + steal
        long nonIdle = user + nice + system + irq + softirq + steal + guest + guest_nice;

        // PrevTotal = PrevIdle + PrevNonIdle
        // Total = Idle + NonIdle
        long total = idle + nonIdle;

        // # differentiate: actual value minus the previous one
        // totald = Total - PrevTotal
        // idled = Idle - PrevIdle
        long totalDelta = total - cpuPrevTotalTime;
        long idleDelta = idle - cpuPrevIdleTime;

        // Update previous times for future deltas
        cpuPrevTotalTime = total;
        cpuPrevIdleTime = idle;

        // CPU_Percentage = (totald - idled)/totald
        return (int)Math.round((totalDelta - idleDelta) * 100 / (double)totalDelta);
    }

    /**
     * Get the instantaneous ARM frequency at this time in MHz.
     * 
     * @return CPU clock frequency in MHz.
     */
    public static int getCPUFrequencyMHz() {
        return getDomainClockSpeed("arm");
    }

    private static long prevV3DTimestamp = 0;
    private static final long[] prevV3DCounters = new long[5];

    /**
     * Report the current utilization of the maximally used hardware unit on the
     * Raspberry Pi iGPU. Possible units are bin, render, tfu, csd, and cache_clean.
     * 
     * @return The percent utilization of the hardware block.
     */
    public static int getGPUUtilization() {
        // Pi 5 way to read the bus: /sys/devices/platform/axi/1002000000.v3d/gpu_stats
        String gpuStats = Utils.readTextFile("/sys/devices/platform/axi/1002000000.v3d/gpu_stats");

        // Pi 4 way to read the bus: /sys/devices/platform/v3dbus/fec00000.v3d/gpu_stats
        if (gpuStats == null) {
            gpuStats = Utils.readTextFile("/sys/devices/platform/v3dbus/fec00000.v3d/gpu_stats");

            // Probably running on an unsupported device?
            if (gpuStats == null) {
                Log.logError("perfmon: unable to read V3D profiling counters");
                return -1;
            }
        }

        // Indices in question: [1, 5] (bin, render, tfu, csd, cache_clean)
        String[] lines = gpuStats.split("\n");
        long[] counters = new long[5];
        long[] counterDeltas = new long[5];

        // Determine time delta from the last time we called this.
        long timestamp = Long.parseLong(lines[1].split("\t")[1].strip());
        long timeDelta = timestamp - prevV3DTimestamp;
        prevV3DTimestamp = timestamp;

        long maxDelta = 0;

        for (int i = 0; i < counters.length; i++) {
            counters[i] = Long.parseLong(lines[i+1].split("\t")[3].strip());
            counterDeltas[i] = counters[i] - prevV3DCounters[i];

            // Find the maximally used hardware unit and report that utilization.
            if (counterDeltas[i] > maxDelta)
                maxDelta = counterDeltas[i];
        }

        // Update counters (to keep deltas on track)
        System.arraycopy(counters, 0, prevV3DCounters, 0, counters.length);

        return (int)Math.round((maxDelta * 100.) / timeDelta);
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
            Log.logError("perfmon: failed to read SoC temp");
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
     * @param flagName The memory region we want.
     * @return The memory region size in kB
     */
    private static int getMemoryTypeMB(String flagName) {
        String meminfo = Utils.readTextFile("/proc/meminfo");

        if (meminfo == null) {
            Log.logError("perfmon: failed to read meminfo");
            return 0; // Can't read memory info? You must have NO RAM >:D
        }

        String[] memFlags = meminfo.split("\n");
        String requestedFlag = null;

        for (String memFlag : memFlags) {
            if (memFlag.contains(flagName))
                requestedFlag = memFlag;
        }

        if (requestedFlag == null)
            throw new RuntimeException("unable to find flag " + flagName + " in /proc/meminfo");

        long memFlagKB = Long.parseLong(requestedFlag.split("\s+")[1].strip());
        return (int)(memFlagKB / 1024);
    }
}
