package yields.client.exceptions;

/**
 * Exception used when an extra is missing on the onCreate method of an activity
 */
public class MissingIntentExtraException extends RuntimeException {
    public MissingIntentExtraException(String message){
        super(message);
    }
}
