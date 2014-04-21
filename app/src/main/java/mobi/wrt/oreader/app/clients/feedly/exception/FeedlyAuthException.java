package mobi.wrt.oreader.app.clients.feedly.exception;

import java.io.IOException;

/**
 * Created by Uladzimir_Klyshevich on 4/21/2014.
 */
public class FeedlyAuthException extends IOException {

    public FeedlyAuthException() {
        super();
    }

    public FeedlyAuthException(String detailMessage) {
        super(detailMessage);
    }

    public FeedlyAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeedlyAuthException(Throwable cause) {
        super(cause);
    }
}
