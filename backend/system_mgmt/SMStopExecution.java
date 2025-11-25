package backend.system_mgmt;

/**
 * Useful for permanently halting execution of a recurring/thread job.
 */
public class SMStopExecution extends Exception {
    public SMStopExecution() {
        super("stop execution requested; stop scheduling this function");
    }
}