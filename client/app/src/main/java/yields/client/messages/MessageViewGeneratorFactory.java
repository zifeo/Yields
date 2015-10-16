package yields.client.messages;

public class MessageViewGeneratorFactory {

    private MessageViewGeneratorFactory(){};

    public static MessageViewGenerator getMessageViewGenerator(Content content)
            throws MessageViewGenerationException
    {
        String contentType = content.getType();
        switch(contentType) {
            case "text":
                return new TextMessageViewGenerator();
            default:
                throw new MessageViewGenerationException();
        }
    }
}


