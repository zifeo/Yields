package yields.client.exceptions;

/**
 * Exception class thrown by the messages package.
 */
public class MessageException extends Exception{
    public MessageException(){
        super();
    }

    public MessageException(String message){
        super(message);
    }

    public MessageException(Throwable th){
        super(th);
    }
}
