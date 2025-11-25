package backend;

/**
 * Callback for hide event. Only called once, then removed from the queue.
 */
public interface HideWindowCallback {
    public void run();
}
