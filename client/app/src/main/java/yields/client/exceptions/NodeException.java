package yields.client.exceptions;

/**
 * Exception class thrown by Nodes.
 */
public class NodeException extends RuntimeException {

    public NodeException() {
        super();
    }

    public NodeException(String message) {
        super(message);
    }

    public NodeException(Throwable th) {
        super(th);
    }
}
