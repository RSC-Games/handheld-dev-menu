package backend.system_mgmt;

class SystemManagementJob {
    private static long NS_TO_S = 1_000_000_000L;

    /**
     * Job function to execute (executed once)
     */
    SMCallbackFunction callback;

    /**
     * The execution mode of this thread.
     */
    Type jobType;
    
    /**
     * Time (in seconds) before job invocation.
     */
    int jobDelay;

    // Set when the job is executed (after the time delay expires.)
    boolean jobScheduled;
    long jobPostedTime;

    enum Type {
        /**
         * Normal job executed once after the time delay.
         */
        JOB_NORMAL,

        /**
         * Job executed repeatedly after the time delay. 
         */
        JOB_RECURRING,

        /**
         * Job is spun off into its own thread and executed. It is no longer managed
         * by the management state machine.
         */
        JOB_STAY_RESIDENT
    }

    SystemManagementJob(SMCallbackFunction cbFun, Type type, int delay) {
        this.callback = cbFun;
        this.jobType = type;
        this.jobDelay = delay;
        this.jobScheduled = false;
        this.jobPostedTime = System.nanoTime();
    }

    /**
     * Run the provided job.
     */
    void executeJob() throws SMStopExecution {
        jobScheduled = true;

        switch (this.jobType) {
            // Mark the job as ready for rescheduling.
            case JOB_RECURRING:
                callback.run();
                jobScheduled = false;
                jobPostedTime = System.nanoTime();
                break;

            // Normal job executes, then returns to be unqueued.
            case JOB_NORMAL:
                callback.run();
                break;
            
            // (Mostly) unmanaged thread that runs in the background (lifetime can still be
            // tracked though)
            case JOB_STAY_RESIDENT:
                // TODO: Thread leak (unmanaged by the system management thread)
                Thread jobThread = new Thread(this.callback, "job-thread-" + this.callback);
                jobThread.start();
                break;
        }
    }

    /**
     * Execute the job if one is scheduled. Blocks until the job is completed,
     * or returns if the job stays resident in its own thread.
     * 
     * @return Whether the job should be considered done and executed.
     */
    boolean executeJobIfScheduled() throws SMStopExecution {
        if (!jobScheduled && timerElapsed()) {
            executeJob();
            return true;
        }

        return false;
    }

    private boolean timerElapsed() {
        return System.nanoTime() >= this.jobPostedTime + (this.jobDelay * NS_TO_S);
    }

    Type getJobType() {
        return this.jobType;
    }
}
