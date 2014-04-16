package mobi.wrt.oreader.app.clients.twitter.exception;

public class TwitterAuthException extends IllegalStateException {

    public TwitterAuthException() {
    }

    public TwitterAuthException(String detailMessage) {
        super(detailMessage);
    }

    public TwitterAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwitterAuthException(Throwable cause) {
        super(cause);
    }
}
