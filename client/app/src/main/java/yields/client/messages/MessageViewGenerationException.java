package yields.client.messages;

public class MessageViewGenerationException extends Exception{

    private static final long serialVersionUID = -1; //TODO

    /**
     * Constructs a new {@code MessageViewGenerationException} that includes the
     * current stack trace.
     */
    public MessageViewGenerationException() {
    }

    /**
     * Constructs a new {@code MessageViewGenerationException} with the current stack
     * trace and the specified detail message.
     *
     * @param detailMessage
     *            the detail message for this exception.
     */
    public MessageViewGenerationException(String detailMessage) {
        super(detailMessage);
    }
}
