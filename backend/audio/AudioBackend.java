package backend.audio;

import backend.CommandUtils;
import backend.CommandUtils.CommandOutput;

public class AudioBackend {
    // Wireplumber commands:
    //
    // wpctl inspect 49
    //  .....
    //     node.name = "auto_null" (for looking the node up again)
    //  .....
    //

    /**
     * Trampoline function; calls the function within the plugin.
     * @see WirePlumberPlugin.listSinks()
     */
    public static AudioSink[] listSinks() {
        return WirePlumberPlugin.listSinks();
    }

    /**
     * Trampoline function; calls the function within the plugin.
     * Gets the default sink instead of returning all of them.
     * 
     * @see WirePlumberPlugin.listSinks()
     */
    public static AudioSink getDefaultSink() {
        return WirePlumberPlugin.getDefaultSink();
    }

    /**
     * Trampoline function; calls the function within the plugin.
     * @see WirePlumberPlugin.setDefaultSink()
     */
    public static void setDefaultSink(AudioSink sink) {
        WirePlumberPlugin.setDefaultSink(sink);
    }

    /**
     * Trampoline function; calls the function within the plugin.
     * @see WirePlumberPlugin.getVolume()
     */
    public static int getVolume() {
        return WirePlumberPlugin.getVolume();
    }

    /**
     * Trampoline function; calls the function within the plugin.
     * @see WirePlumberPlugin.getMute()
     */
    public static boolean getMuted() {
        return WirePlumberPlugin.getMuted();
    }

    /**
     * Trampoline function; calls the function within the plugin.
     * @see WirePlumberPlugin.setVolume()
     */
    public static void setVolume(int percent) {
        WirePlumberPlugin.setVolume(percent);
    }

    /**
     * Trampoline function; calls the function within the plugin.
     * @see WirePlumberPlugin.setMute()
     */
    public static void setMute(boolean muted) {
        WirePlumberPlugin.setMute(muted);
    }

    /**
     * Internal plugin implementation. Provides support for Wireplumber's
     * audio backend. Other backends could be supported in the future.
     * 
     * All of these backend functions operate on the default sink.
     */
    private static class WirePlumberPlugin {
        private static final String DEFAULT_SINK = "@DEFAULT_AUDIO_SINK@";

        /**
         * Gets a list of all sinks available.
         * 
         * wpctl status -n
         * (prints out chars like ├, ─, │, └; should be replaced w/ \t"
         * PipeWire 'pipewire-0' [1.4.8, system@nsa-cbc-vm, cookie:??????????]
         *  └─ Clients: (We don't care abt this part)
         *         (spaces, not tabs) 32. kwin_wayland         [1.4.8, system@nsa-cbc-vm, pid=???]
         *
         * Audio
         *  ├─ Devices:
         *  │   <-- ('|  ' means empty line)
         *  ├─ Sinks:
         *  │  *   49. auto_null                              [vol: 1.00]
         *     * is default, '   ' before the id, '. ' before the sink name, then volume
         *  │
         *  ├─ Sources:
         *  │      (Will want to parse these later when mic support is added)
         *  ├─ Filters:
         *  │
         *  └─ Streams:
         *
         * Video (don't care)
         */
        static AudioSink[] listSinks() {
            return null; // TODO: Parse all of the sinks and give a full list.
        }

        /**
         * Probably shouldn't be used for the most part.
         * 
         * @return
         */
        static AudioSink getDefaultSink() {
            for (AudioSink sink : listSinks())
                if (sink.isDefault)
                    return sink;

            return null;
        }

        /**
         * Set the current default audio sink (as used by applications).
         * 
         * wpctl set-default 49
         *  (returns nothing if successful)
         */
        static void setDefaultSink(AudioSink sink) {
            CommandOutput output = CommandUtils.executeCommandRetry("wpctl", "set-default", "" + sink.id);

            if (!executedSuccessfully(output))
                System.err.println("error while setting default sink");
        }

        /**
         * Get the volume of the default sink.
         * 
         * wpctl get-volume 49
         *  Volume: 1.00 [MUTED] (only shows if muted)
         * 
         * @return Volume of the default sink.
         */
        static int getVolume() {
            CommandOutput output = CommandUtils.executeCommandRetry("wpctl", "get-volume", DEFAULT_SINK);

            if (!executedSuccessfully(output)) {
                System.err.println("error determining card volume");
                return 0;
            }

            String[] volumeComponents = output.getStdout().strip().split("\s");
            return Math.round(Float.parseFloat(volumeComponents[1]) * 100);
        }

        /**
         * Get the mute status of the default sink.
         * 
         * @return If it's muted.
         */
        static boolean getMuted() {
            CommandOutput output = CommandUtils.executeCommandRetry("wpctl", "get-volume", DEFAULT_SINK);

            if (!executedSuccessfully(output))
                return true;

            String[] volumeComponents = output.getStdout().strip().split("\s");

            // [MUTED] flag doesn't show up when unmuted.
            if (volumeComponents.length != 3)
                return false;

            return volumeComponents[2].equals("[MUTED]");
        }

        /**
         * Set the volume of the default sink.
         * 
         * wpctl set-volume 49 1%
         *  (returns nothing if successful)
         * 
         * @param volume New volume to set for the sink
         */
        static void setVolume(int volume) {
            CommandOutput output = CommandUtils.executeCommandRetry("wpctl", "set-volume", DEFAULT_SINK, volume + "%");

            executedSuccessfully(output);
        }

        static void setMute(boolean muted) {
            CommandOutput output = CommandUtils.executeCommandRetry(
                "wpctl", 
                "set-volume", 
                DEFAULT_SINK, 
                muted ? "1" : "0"
            );

            executedSuccessfully(output);
        }

        private static boolean executedSuccessfully(CommandOutput output) {
            if (output == null)  {
                System.err.println("failed to run wpctl (reason: not installed)");
                return false;
            }

            else if (output.getExitCode() != 0) {
                System.err.println("failed to execute for some odd reason. stderr:");
                System.err.println(output.getStderr());
                return false;
            }

            return true;
        }
    }
}
