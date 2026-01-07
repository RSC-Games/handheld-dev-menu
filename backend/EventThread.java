package backend;

import java.util.concurrent.ArrayBlockingQueue;

import util.Log;
import util.Utils;

/**
 * The event thread handles executing code asynchronously from the main threads.
 * IT IS NOT RECOMMENDED TO ACCESS ANY MEMBERS OF THIS CLASS, PUBLIC OR PRIVATE!
 * It is about as unsafe as you can get with Java without raw memory pointers
 * and relies on language features that shouldn't really even exist.
 * 
 * Should change EventThread to use a type-safe callbacks system...
 * But then how would I query return values?
 */
@SuppressWarnings("rawtypes")
final class EventThread implements Runnable {
    static final EventThread theThread;

    static {
        theThread = new EventThread();
        Thread evThread = new Thread(theThread, "Background Event Thread");
        evThread.setDaemon(true);
        evThread.start();
    }

    private final ArrayBlockingQueue<EVTCommand> fifoIn;
    private final ArrayBlockingQueue<EVTResponse> fifoOut;
    private volatile EVTCommand executingCommand;

    EventThread() {
        this.fifoIn = new ArrayBlockingQueue<>(32);
        this.fifoOut = new ArrayBlockingQueue<>(32);
    }

    /**
     * Determine if a job is still waiting in the job queue for scheduling purposes.
     * 
     * @param commandID The command name
     * @return Whether it's in the job queue
     */
    public static boolean getJobQueued(String commandID) {
        return theThread.jobInFifo(commandID) | theThread.jobIsExecuting(commandID);
    }

    /**
     * Get the queued up command response.
     * 
     * @param <T> The type of the response.
     * @param commandID The command to execute.
     * @return The response.
     */
    public static <T> EVTResponse<T> getResponse(String commandID) {
        return theThread.<T>getJobResponse(commandID);
    }

    /**
     * Determine if the requested job is completed.
     * 
     * @param commandID The command to 
     * @return
     */
    public static boolean jobComplete(String commandID) {
        return theThread.getJobComplete(commandID);
    }

    /**
     * Queue a job for execution by this thread.
     * 
     * @param <T> The type of the command
     * @param command The command to execute.
     */
    public static <T> void queueJob(EVTCommand<T> command) {
        theThread.<T>pushJob(command);
    }

    /**
     * Find the job in the input fifo, if any.
     * 
     * @param commandID The command name
     * @return If it's in the fifo.
     */
    private boolean jobInFifo(String commandID) {
        for (EVTCommand command : fifoIn)
            if (command.getCommand().equals(commandID))
                return true;

        return false;
    }

    /**
     * Determine if the job has been completed successfully.
     * 
     * @param commandID The command name
     * @return If it's in the fifo.
     */
    private boolean getJobComplete(String commandID) {
        for (EVTResponse response : fifoOut)
            if (response.getCommand().equals(commandID))
                return true;

        return false;
    }

    /**
     * Get the job response data.
     * 
     * @param commandID The command name
     * @return If it's in the fifo.
     */
    @SuppressWarnings("unchecked")
    private <T> EVTResponse<T> getJobResponse(String commandID) {
        for (EVTResponse response : fifoOut) {
            if (response.getCommand().equals(commandID)) {
                fifoOut.remove(response);
                return (EVTResponse<T>)response;
            }
        }

        return null;
    }

    /**
     * Determine if the currently executing job is this job type.
     * 
     * @param commandID The job type in question
     * @return Whether it's actively executing.
     */
    private boolean jobIsExecuting(String commandID) {
        if (executingCommand == null)
            return false;

        return executingCommand.getCommand().equals(commandID);
    }

    /**
     * Push a job to the job queue and don't return until it's verified to have been
     * pushed.
     * 
     * @param <T> The type of the job to push
     * @param job The job to push.
     */
    private <T> void pushJob(EVTCommand<T> job) {
        while (true) {
            try {
                if (fifoIn.contains(job))
                    break;

                fifoIn.put(job);
                break;
            }
            catch (InterruptedException ie) {}
        }
    }

    @Override
    public void run() {
        while (true) {
            if (this.fifoIn.size() == 0) {
                Utils.sleepms(16);
                continue;
            }

            executingCommand = fifoIn.poll();
            EVTResponse res = execute(executingCommand);
            putFifoRetry(res);
            executingCommand = null;
        }
    }

    /**
     * Massive ugly dispatcher function. Wraps the insanity of this class and
     * pretends like there's a pretty little interface waiting.
     * 
     * @param command
     * @return
     */
    private EVTResponse execute(EVTCommand command) {
        switch (command.command) {
            case "wpa_cli_status":
                Log.logVerbose("event_handler: async executing wpa_cli_status");
                return EventThreadTrampolines.wpaCliStatusTrampoline(command);
            default:
                Log.logInfo("event_handler: issued unrecognized command " + command.command);
        }

        return null;
    }

    /**
     * Ensure the response always ends up in the fifo no matter what happens.
     * @param res The command response.
     */
    private void putFifoRetry(EVTResponse res) {
        while (true) {
            try {
                if (fifoOut.contains(res))
                    break;

                fifoOut.put(res);
                break;
            }
            catch (InterruptedException ie) {}
        }
    }
}

/**
 * Command format. Most commands expect T to be an Object[] of some kind
 * with a list of parameters.
 */
class EVTCommand<T> {
    long commandUUID;
    String command;
    T param;

    EVTCommand(String command, T param) {
        this.command = command;
        this.param = param;
    }

    String getCommand() {
        return this.command;
    }

    T getParam() {
        return this.param;
    }
}

class EVTResponse<T> {
    String command;
    T res;

    EVTResponse(String command, T res) {
        this.command = command;
        this.res = res;
    }

    String getCommand() {
        return this.command;
    }

    T getResponse() {
        return this.res;
    }
}
