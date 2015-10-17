package yields.client.messages;

import yields.client.node.Node;
import yields.client.node.User;

/**
 * Message is a {@code Node} which is shared as a message in a conversation.
 */
public class Message {

    private final Node mNode;
    private final User mSender;
    private final Content mContent;

    public Message(Node node, User sender, Content content){
        this.mNode = node;
        this.mSender = sender;
        this.mContent = content;
    }

    public Node getNode(){return mNode};

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