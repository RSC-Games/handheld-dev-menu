package backend;

import java.util.HashMap;

@SuppressWarnings("rawtypes")
class EventThreadTrampolines {

    /**
     * Wrap the nasty complexity of the event thread system and convert it to a clean call
     * to NetworkBackend.status0();
     * 
     * @param input The input (of Void type since nothing important is there)
     * @return The normal hashmap except encoded into the event thread system.
     */
    static EVTResponse<HashMap<String, String>> wpaCliStatusTrampoline(EVTCommand input) {
        return new EVTResponse<HashMap<String, String>>(input.command, NetworkBackend.status0());
    }
}
