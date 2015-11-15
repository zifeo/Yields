package yields.client.exceptions;

/**
 * Exception class for ServiceRequests.
 */
public class ServiceRequestException extends RuntimeException {

    public ServiceRequestException() {
        super();
    }

    public ServiceRequestException(String message) {
        super(message);
    }

    public ServiceRequestException(Throwable th) {
        super(th);
    }
}