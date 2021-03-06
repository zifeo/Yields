package yields.client.exceptions;

/**
 * Exception class thrown by Content interface.
 */
public class ContentException extends RuntimeException {

    public ContentException() {
        super();
    }

    public ContentException(String message) {
        super(message);
    }

    public ContentException(Throwable th) {
        super(th);
    }
}
