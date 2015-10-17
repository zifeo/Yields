package yields.client.messages;

import yields.client.node.User;

/**
 * Message is a {@code Node} which is shared as a message in a conversation.
 */
public class Message {

    private final User mSender;
    private final Content mContent;

    public Message(User sender, Content content){
        this.mSender = sender;
        this.mContent = content;
    }

    public User getSender() {return mSender;}

    public Content getContent() {
        return mContent;
    }

    public MessageView getMessageView(){
        try {
            MessageViewGenerator generator = MessageViewGeneratorFactory.getMessageViewGenerator(mContent);
            return generator.generateMessageView(this);
        }catch (MessageViewGenerationException e){
            return null; //TODO : Decide what to do in this case
        }
    }
}