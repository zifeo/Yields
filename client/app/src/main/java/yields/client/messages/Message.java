package yields.client.messages;


import android.graphics.Bitmap;
import android.util.Log;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

import yields.client.exceptions.MessageException;
import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.ImageSerialization;
import yields.client.yieldsapplication.YieldsApplication;


/**
 * Message is a {@code Node} which is shared as a message in a conversation.
 */
public class Message {

    public enum MessageStatus {
        SENT("SENT"), SEEN("SEEN"), NOT_SENT("NOT_SENT");

        private String mValue;

        MessageStatus(String value) {
            mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }

    private final Id mSender;
    private final Content mContent;
    private Date mDate;
    private MessageStatus mStatus;
    private MessageView mView;
    private Id mCommentGroupId;

    /**
     * Main constructor for a Message.
     *
     * @param nodeID  ID of the Node for a commentGroup.
     * @param sender  The sender of the message.
     * @param content The content of the message.
     * @param status  The status of the message.
     * @throws MessageException If the message content or sender is incorrect.
     * @throws NodeException    If the Node information is incorrect.
     */
    public Message(Id nodeID, Id sender, Content content,
                   Date date, MessageStatus status) {
        this.mCommentGroupId = nodeID;
        this.mSender = Objects.requireNonNull(sender);
        this.mContent = Objects.requireNonNull(content);
        this.mDate = new Date(date.getTime());
        this.mStatus = status;
        this.mView = new MessageView(YieldsApplication.getApplicationContext(), this);
    }

    /**
     * Constructor for a Message, sets it's MessageStatus to NOT_SENT.
     *
     * @param nodeID  ID of the Node.
     * @param sender  The sender of the message.
     * @param content The content of the message.
     * @throws MessageException If the message content or sender is incorrect.
     * @throws NodeException    If the Node information is incorrect.
     */
    public Message(Id nodeID, Id sender, Content content, Date date) {
        this(nodeID, sender, content, date, MessageStatus.NOT_SENT);
    }

    /**
     * Constructor of a message from the JSON fields received from the server.
     *
     * @param dateTime    The date in String format.
     * @param senderID    The is of the sender in String format.
     * @param text        The text of the message (if it is a text message, null otherwise).
     * @param contentType The content type of the message.
     * @param content     The content of the message.
     * @throws ParseException In case of parse exception with the date serialization.
     */
    public Message(String dateTime, Long contentId, Long senderID, String text, String contentType, String content) throws ParseException {
        this.mCommentGroupId = new Id(Objects.requireNonNull(contentId));
        this.mSender = new Id(senderID);

        if (contentType.equals("image")) {
            Bitmap img = ImageSerialization.unSerializeImage(content);
            if (img == null) {
                Log.d("Y:" + this.getClass().getName(), "We have no image with contentType image");
                mContent = new TextContent(text);
            } else {
                mContent = new ImageContent(img, text);
            }

        } else if (contentType.equals("url")) {
            this.mContent = new UrlContent(text);
        } else {
            this.mContent = new TextContent(text);
        }

        this.mDate = DateSerialization.dateSerializer.toDate(dateTime);
        mStatus = MessageStatus.SENT;
        this.mView = new MessageView(YieldsApplication.getApplicationContext(), this);
    }

    /**
     * Returns the sender of the message.
     *
     * @return The sender of the message.
     */
    public Id getSender() {
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
     * Returns a preview of the message, displayed in the group list.
     *
     * @return A string describing the message.
     */
    public String getPreview() {
        return YieldsApplication.getNodeFromId(mSender).getName() + " : " + mContent.getPreview();
    }

    /**
     * Returns the MessageStatus of the message.
     *
     * @return The MessageStatus of the message.
     */
    public MessageStatus getStatus() {
        return mStatus;
    }

    /**
     * returns the comment group id
     */
    public Id getCommentGroupId() {
        return mCommentGroupId;
    }

    /**
     * sets the comment group id
     */
    public void setCommentGroupId(Id mCommentGroupId) {
        this.mCommentGroupId = mCommentGroupId;
    }

    /**
     * Sets the MessageStatus of the message.
     *
     * @param messageStatus The MessageStatus of the message.
     */
    public void setStatus(MessageStatus messageStatus) {
        mStatus = messageStatus;
    }

    /**
     * Sets the MessageStatus of the message and updated it's date.
     *
     * @param messageStatus The MessageStatus of the message.
     * @param timeStamp     The updated Date of the message
     */
    public void setStatusAndUpdateDate(MessageStatus messageStatus, Date timeStamp) {
        setStatus(messageStatus);
        mDate = timeStamp;
    }

    /**
     * Recomputes the MessageView for this Message.
     * This is used to recompute the view, if it has been updated.
     */
    public void recomputeView() {
        mView = new MessageView(YieldsApplication.getApplicationContext(), this);
    }

    /**
     * Returns the MessageView associated to this message.
     *
     * @return The MessageView associated to this message.
     */
    public MessageView getView() {
        return mView;
    }
}