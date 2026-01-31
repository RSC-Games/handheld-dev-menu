package backend.system_mgmt;

import java.util.ArrayList;

import util.Log;
import util.Utils;

// TODO: Run system updates in the background once requested. (Note: will be done on the event thread)
// TODO: System push notifications

/**
 * Handle system management code and other long-running jobs that can't stall the main
 * thread but need to be tracked.
 */
public class SystemManagementThread implements Runnable {
    private static final SystemManagementThread managementSystem;
    private static final Thread managementThread;

    static {
        managementSystem = new SystemManagementThread();
        managementThread = new Thread(managementSystem, "sys-mgr");
        managementThread.setDaemon(true);
        managementThread.start();
    }

    /**
     * Modified on multiple threads, and set to null during modifications to avoid
     * errant writes.
     */
    private volatile ArrayList<SystemManagementJob> jobs;
    private ArrayList<SystemManagementJob> deferredJobs;

    private SystemManagementThread() {
        jobs = new ArrayList<>();
        deferredJobs = new ArrayList<>();
    }

    /**
     * Call this function on the system management dispatch thread after the given delay
     * 
     * @param delay Time to wait (in seconds)
     * @param cb The callback to execute.
     */
    public static void invokeDelayed(int delay, SMCallbackFunction cb) {
        managementSystem.pushJob(new SystemManagementJob(cb, SystemManagementJob.Type.JOB_NORMAL, delay));
    }

    /**
     * Call this function on the system management dispatch thread after the given delay.
     * Deferred is required for pushing jobs within an existing job handler.
     * @implNote THE SYSTEM THREAD WILL DEADLOCK IF THE NON-DEFERRED VARIANT IS CALLED!
     * 
     * @param delay Time to wait (in seconds)
     * @param cb The callback to execute.
     */
    public static void invokeDelayedDeferred(int delay, SMCallbackFunction cb) {
        managementSystem.pushJobDeferred(new SystemManagementJob(cb, SystemManagementJob.Type.JOB_NORMAL, delay));
    }

    /**
     * Push a repeating job for repeated queuing and execution.
     * 
     * @param delay Time between invocations (in seconds)
     * @param cb The callback to execute.
     */
    public static void repeatingJob(int delay, SMCallbackFunction cb) {
        managementSystem.pushJob(new SystemManagementJob(cb, SystemManagementJob.Type.JOB_RECURRING, delay));
    }

    /**
     * Push a repeating job for repeated queuing and execution.
     * Deferred is required for pushing jobs within an existing job handler.
     * @implNote THE SYSTEM THREAD WILL DEADLOCK IF THE NON-DEFERRED VARIANT IS CALLED!
     * 
     * @param delay Time between invocations (in seconds)
     * @param cb The callback to execute.
     */
    public static void repeatingJobDeferred(int delay, SMCallbackFunction cb) {
        managementSystem.pushJobDeferred(new SystemManagementJob(cb, SystemManagementJob.Type.JOB_RECURRING, delay));
    }

    private synchronized void pushJob(SystemManagementJob job) {
        if (Thread.currentThread().equals(managementThread))
            throw new IllegalStateException("cannot synchronously push job from management thread!");

        // Currently being modified; wait for release
        while (jobs == null)
            Utils.sleepms(1);

        Log.logVerbose("sm_thread: pushing synchronous job " + job);
        jobs.add(job);
    }

    private void pushJobDeferred(SystemManagementJob job) {
        if (!Thread.currentThread().equals(managementThread))
            throw new IllegalStateException("deferred push is not thread safe!");

        Log.logVerbose("sm_thread: pushing deferred job " + job);
        deferredJobs.add(job);
    }

    public void run() {
        while (true) {
            // These jobs aren't hugely time sensitive, so give time back to other threads
            // (The target hardware only has a few cores anyway)
            Utils.sleepms(33);

            executeJobs();
        }
    }

    private synchronized void executeJobs() {
        // Nothing to do.
        if (this.jobs.size() == 0 && this.deferredJobs.size() == 0)
            return;

        // TOCTOU but it doesn't really matter in this context b/c we'll just execute
        // the jobs next batch.

        // Possible race condition? (functions should be locking anyway due to synchronized
        // so this shouldn't be an issue)
        // Other than language features java doesn't have any intrinsics afaik
        ArrayList<SystemManagementJob> jobsCopy = this.jobs;
        this.jobs = null;

        // Take all the deferred jobs and queue them for execution.
        jobsCopy.addAll(this.deferredJobs);
        this.deferredJobs.clear();

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
                Log.logVerbose("sm_thread: job " + job + " requested termination; removing from queue");
            }
            catch (Exception ie) {
                Log.logError("sm_thread: fatal exception in job; force unqueuing. exception details:");
                ie.printStackTrace();
                Log.logException(ie);
            }
        }

        this.jobs = rescheduleJobs;
    }
}