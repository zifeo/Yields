package yields.client.messages;

import yields.client.node.User;
import yields.client.id.Id;
import yields.client.node.Node;


/**
 * Message is a {@code Node} which is shared as a message in a conversation.
 */
public class Message extends Node{

    private final User mSender;
    private final Content mContent;
    private final java.util.Date mDate;

    public Message(String nodeName, Id nodeID, User sender, Content content){
        super(nodeName, nodeID);
        this.mSender = sender;
        this.mContent = content;
        this.mDate = new java.util.Date();
    }

    public User getSender(){
        return mSender;
    }

    public Content getContent() {
        return mContent;
    }

    public java.util.Date getDate(){
        return new java.util.Date(mDate.getTime());
    }
}