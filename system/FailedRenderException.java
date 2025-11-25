package system;

class FailedRenderException extends RuntimeException {
    FailedRenderException(Throwable cause) {
        super("Exception occurred during render pass", cause);
    }
}
