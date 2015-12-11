package yields.client.exceptions;

public class CommentViewException extends RuntimeException {
    public CommentViewException() {
        super();
    }

    public CommentViewException(String message) {
        super(message);
    }

    public CommentViewException(Throwable th) {
        super(th);
    }
}
