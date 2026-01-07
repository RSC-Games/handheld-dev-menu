package backend.audio;

import java.util.ArrayList;

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
         * 
         * Settings
         *  └─ Default Configured Devices:
         *          0. Audio/Sink    bluez_output.88_0E_85_85_44_B8.1
         *          1. Audio/Source  alsa_input.usb-046d_HD_Webcam_C615_.....
         */
        static AudioSink[] listSinks() {
            // For the human readable name, exclude -n at the end.
            CommandOutput output = CommandUtils.executeCommandRetry("wpctl", "status", "-n");

            // We only care about two regions for this: Audio and Settings.
            // All of these are split by two newlines, which makes it pretty easy to parse.
            String[] segments = output.getStdout().split("\n\n");

            // wpctl outputs 4 segments: Clients, Audio, Video, and Settings (in this order).
            String audioSinksList = segments[1].split("├─")[2];
            String defaultsList = segments[3];

            // Filter out the header and only capture each new sink, excluding the blank last line.
            String[] sinks = audioSinksList.substring(audioSinksList.indexOf("\n"), 
                                                      audioSinksList.lastIndexOf("\n")).split("\n\s+│\s+");

            // Note: assuming the third index (first 2 are just for readability) is the default sink. 
            // THIS ASSUMPTION MAY NOT ALWAYS HOLD!!!
            String defaultSinkName = defaultsList.split("\n")[3];

            ArrayList<AudioSink> foundSinks = new ArrayList<>();

            for (String sink : sinks) {
                if (sink.strip().equals(""))
                    continue;

                // Format: ID. NAME [vol: 0.xx]
                String sinkID = sink.substring(0, sink.indexOf(".")).strip();
                String sinkName = sink.substring(sink.indexOf(".") + 1, sink.lastIndexOf("[")).strip();

                // Default sink is going to have the same name as the, well, default sink...
                boolean isDefault = defaultSinkName.contains(sinkName);

                foundSinks.add(new AudioSink(
                    isDefault, 
                    Integer.parseInt(sinkID), 
                    sinkName)
                );
            }

            return foundSinks.toArray(AudioSink[]::new);
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

        static void setMute(boolean shouldMute) {
            CommandOutput output = CommandUtils.executeCommandRetry(
                "wpctl", 
                "set-mute", 
                DEFAULT_SINK, 
                shouldMute ? "1" : "0"
            );

            executedSuccessfully(output);
        }

        private static boolean executedSuccessfully(CommandOutput output) {
            if (output == null)  {
                System.err.println("failed to run wpctl (reason: not installed)");
                return false;
            }

            if (output.getExitCode() != 0) {
                System.err.println("failed to execute for some odd reason. stderr:");
                System.err.println(output.getStderr());
                return false;
            }

            return true;
        }
    }
}
