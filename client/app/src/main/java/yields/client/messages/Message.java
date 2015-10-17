package yields.client.messages;

import yields.client.node.Node;
import yields.client.node.User;

/**
 * Message is a {@code Node} which is shared as a message in a conversation.
 */
public class Message extends Node{

    private final User mSender;
    private final Content mContent;

    public Message(String nodeName, long nodeID, User sender, Content content){
        super(nodeName, nodeID);
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