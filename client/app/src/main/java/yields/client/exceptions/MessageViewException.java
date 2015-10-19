package yields.client.exceptions;

/**
 * Exception class thrown by MessageView.
 */
public class MessageViewException extends Exception{

    public MessageViewException(){
        super();
    }

    public MessageViewException(String message){
        super(message);
    }

    public MessageViewException(Throwable th){
        super(th);
    }
}
