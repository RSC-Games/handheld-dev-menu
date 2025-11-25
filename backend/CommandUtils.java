package backend;

import java.io.IOException;

public class CommandUtils {
        /**
     * Retry command execution until external factors other than errors within the command
     * itself are eliminated.
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

                System.out.println("Warning: retrying command " + args[0] + " <args redacted>");
            } 
            catch (IllegalStateException ie) {
                //ie.printStackTrace();
                return null;
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
            //System.out.println("An error occurred while executing the command:");
            //ie.printStackTrace();
            throw new IllegalStateException("Unable to execute command: " + args[0], ie);
        }
        catch (InterruptedException ie) {
            System.out.println("Interrupted during execution");
            ie.printStackTrace();
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