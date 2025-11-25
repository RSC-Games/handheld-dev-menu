package backend.system_mgmt;

import java.util.ArrayList;

import util.Utils;

// TODO: Run system updates in the background once requested. (Note: will be done on the event thread)
// TODO: System push notifications

/**
 * Handle system management code and other long-running jobs that can't stall the main
 * thread but need to be tracked.
 */
public class SystemManagementThread implements Runnable {
    private static SystemManagementThread managementThread;

    static {
        managementThread = new SystemManagementThread();
        Thread thread = new Thread(managementThread, "sys-mgr");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Modified on multiple threads, and set to null during modifications to avoid
     * errant writes.
     */
    private volatile ArrayList<SystemManagementJob> jobs;

    private SystemManagementThread() {
        jobs = new ArrayList<>();
    }

    /**
     * Call this function on the system management dispatch thread after the given delay
     * 
     * @param delay Time to wait (in seconds)
     * @param cb The callback to execute.
     */
    public static void invokeDelayed(int delay, SMCallbackFunction cb) {
        managementThread.pushJob(new SystemManagementJob(cb, SystemManagementJob.Type.JOB_NORMAL, delay));
    }

    /**
     * Push a repeating job for repeated queuing and execution.
     * 
     * @param delay Time between invocations.
     * @param cb The callback to execute.
     */
    public static void repeatingJob(int delay, SMCallbackFunction cb) {
        managementThread.pushJob(new SystemManagementJob(cb, SystemManagementJob.Type.JOB_RECURRING, delay));
    }

    private synchronized void pushJob(SystemManagementJob job) {
        // Currently being modified
        while (jobs == null)
            Utils.sleepms(1);

        System.out.println("pushing job");
        jobs.add(job);
    }

    public void run() {
        while (true) {
            // These jobs aren't hugely time sensitive, so give time back to the OS.
            Utils.sleepms(33);

            executeJobs();
        }
    }

    private synchronized void executeJobs() {
        // Nothing to do.
        if (this.jobs.size() == 0)
            return;

        // TOCTOU but it doesn't really matter in this context b/c we'll just execute
        // the jobs next batch.

        // Possible race condition? (functions should be locking anyway due to synchronized
        // so this shouldn't be an issue)
        ArrayList<SystemManagementJob> jobsCopy = this.jobs;
        this.jobs = null;

        ArrayList<SystemManagementJob> rescheduleJobs = new ArrayList<>();

        for (SystemManagementJob job : jobsCopy) {
            try {
                // Job has been executed, and might be recurring, so reschedule
                if (!job.executeJobIfScheduled() || job.getJobType() == SystemManagementJob.Type.JOB_RECURRING)
                    rescheduleJobs.add(job);

                // Job executed and is no longer in the queue.
            }
            catch (SMStopExecution ie) {
                // Job has explicitly requested not to be rescheduled.
                System.out.println("queued job requested explicit cancellation");
            }
        }

        this.jobs = rescheduleJobs;
    }
}