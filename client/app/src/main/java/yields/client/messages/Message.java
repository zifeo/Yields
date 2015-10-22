package yields.client.messages;

import java.util.Objects;

import yields.client.exceptions.MessageException;
import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.node.Node;
import yields.client.node.User;


/**
 * Message is a {@code Node} which is shared as a message in a conversation.
 */
public class Message extends Node{

    private final User mSender;
    private final Content mContent;
    private final java.util.Date mDate;

    /**
     * Main constructor for a Message.
     * @param nodeName Name of the Node.
     * @param nodeID ID of the Node.
     * @param sender The sender of the message.
     * @param content The content of the message.
     * @throws MessageException If the message content or sender is incorrect.
     * @throws NodeException If the Node information is incorrect.
     */
    public Message(String nodeName, Id nodeID, User sender, Content content) {
        super(nodeName, nodeID);
        Objects.requireNonNull(sender);
        Objects.requireNonNull(content);
        this.mSender = sender;
        this.mContent = content;
        this.mDate = new java.util.Date();
    }

    /**
     * Returns the sender of the message.
     * @return The sender of the message.
     */
    public User getSender(){
        return mSender;
    }

    /**
     * Returns the Content of the message.
     * @return The Content of the message.
     */
    public Content getContent() {
        return mContent;
    }

    /**
     * Returns the Date of the message, which is the Date of it's creation.
     * @return The Date of the message's creation.
     */
    public java.util.Date getDate(){
        return new java.util.Date(mDate.getTime());
    }
}