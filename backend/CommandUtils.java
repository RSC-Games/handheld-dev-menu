package backend;

import system.Config;
import java.io.IOException;
import java.util.Arrays;

import util.Log;

public class CommandUtils {
    /**
     * Retry command execution until external factors other than errors within the command
     * itself are eliminated. If no command can be found, stderr/stdout will be null and the
     * return code will be -2^31
     * 
     * @param args Command arguments
     * @return Command outputs
     */
    public static CommandOutput executeCommandRetry(String... args) {
        while (true) {
            try {
                CommandOutput output = executeCommand0(args);

                // Prevent external factors from inhibiting command execution.
                if (output != null)
                    return output;

                Log.logWarning("command_engine: retrying command " + args[0] + (!Config.SHOW_COMMAND_ARGS_IN_LOGS ? 
                               Arrays.toString(args) : " <args redacted>"));
            }
            // Unable to retry command execution; return no output.
            catch (IllegalStateException ie) {
                Log.logError("command_engine: unable to execute provided command. details below");
                Log.logException(ie);
                return new CommandOutput(null, null, Integer.MIN_VALUE);
            }
        }
    }

    static CommandOutput executeCommand0(String... args) {
        try {
            Process process = Runtime.getRuntime().exec(args);
            int exitCode = process.waitFor();

            return new CommandOutput(
                new String(process.getInputStream().readAllBytes()),
                new String(process.getErrorStream().readAllBytes()),
                exitCode
            );
        }
        catch (IOException ie) {
            throw new IllegalStateException("Unable to execute command: " + args[0], ie);
        }
        catch (InterruptedException ie) {
            Log.logWarning("command_engine: thread sent interrupt during command execution- possible bug");
            Log.logException(ie);
        }

        return null;
    }

    public static class CommandOutput {
        private int retCode;
        private String stdOut;
        private String stdErr;

        CommandOutput(String stdout, String stderr, int retCode) {
            this.stdOut = stdout;
            this.stdErr = stderr;
            this.retCode = retCode;
        }

        public int getExitCode() {
            return retCode;
        }

        public String getStdout() {
            return stdOut;
        }

        public String getStderr() {
            return stdErr;
        }
    }
}