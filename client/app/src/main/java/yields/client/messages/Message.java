package yields.client.messages;

import java.util.Date;
import java.util.Objects;

import yields.client.exceptions.MessageException;
import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.node.User;


/**
 * Message is a {@code Node} which is shared as a message in a conversation.
 */
public class Message extends Node {

    private final User mSender;
    private final Content mContent;
    private final Date mDate;
    private final Group mGroupReceiver;

    /**
     * Main constructor for a Message.
     *
     * @param nodeName Name of the Node.
     * @param nodeID   ID of the Node.
     * @param sender   The sender of the message.
     * @param content  The content of the message.
     * @throws MessageException If the message content or sender is incorrect.
     * @throws NodeException    If the Node information is incorrect.
     */
    public Message(String nodeName, Id nodeID, User sender, Content content,
                   Date date, Group groupReceiver) {
        super(nodeName, nodeID);
        this.mSender = Objects.requireNonNull(sender);
        this.mContent = Objects.requireNonNull(content);
        this.mDate = new Date(date.getTime());
        this.mGroupReceiver = Objects.requireNonNull(groupReceiver);
    }

    /**
     * Returns the sender of the message.
     *
     * @return The sender of the message.
     */
    public User getSender() {
        return mSender;
    }

    /**
     * Returns the Content of the message.
     *
     * @return The Content of the message.
     */
    public Content getContent() {
        return mContent;
    }

    /**
     * Returns the Date of the message, which is the Date of it's creation.
     *
     * @return The Date of the message's creation.
     */
    public java.util.Date getDate() {
        return new Date(mDate.getTime());
    }

    /**
     * Returns the Group which receives this message.
     *
     * @return The Group which reveives this message.
     */
    public Group getReceivingGroup() {
        return mGroupReceiver;
    }

    /**
     * Returns a preview of the message, displayed in the group list
     *
     * @return a string describing the message
     */
    public String getPreview() {
        return mSender.getName() + " : " + mContent.getPreview();
    }
}