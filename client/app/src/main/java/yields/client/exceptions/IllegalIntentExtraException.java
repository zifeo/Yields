package yields.client.exceptions;

/**
 * Exception used when an extra has a wrong value in an activity
 */
public class IllegalIntentExtraException extends RuntimeException{
    public IllegalIntentExtraException(String message){
        super(message);
    }
}