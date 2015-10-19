package yields.client.exceptions;

/**
 * Exeption class specific to the message activity.
 */
public class MessageActivityException extends Exception {
    public MessageActivityException(){
        super();
    }

    public MessageActivityException(String message){
        super(message);
    }

    public MessageActivityException(Throwable th){
        super(th);
    }
}
