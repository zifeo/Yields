package yields.client.messages;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

import yields.client.R;
import yields.client.exceptions.MessageException;
import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.yieldsapplication.YieldsApplication;


/**
 * Message is a {@code Node} which is shared as a message in a conversation.
 */
public class Message extends Node {

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

    /**
     * Main constructor for a Message.
     *
     * @param nodeName Name of the Node.
     * @param nodeID   ID of the Node.
     * @param sender   The sender of the message.
     * @param content  The content of the message.
     * @param status   The status of the message.
     * @throws MessageException If the message content or sender is incorrect.
     * @throws NodeException    If the Node information is incorrect.
     */
    public Message(String nodeName, Id nodeID, Id sender, Content content,
                   Date date, MessageStatus status) {
        super(nodeName, nodeID);
        this.mSender = Objects.requireNonNull(sender);
        this.mContent = Objects.requireNonNull(content);
        this.mDate = new Date(date.getTime());
        this.mStatus = status;
    }

    /**
     * Constructor for a Message, sets it's MessageStatus to NOT_SENT.
     *
     * @param nodeName Name of the Node.
     * @param nodeID   ID of the Node.
     * @param sender   The sender of the message.
     * @param content  The content of the message.
     * @throws MessageException If the message content or sender is incorrect.
     * @throws NodeException    If the Node information is incorrect.
     */
    public Message(String nodeName, Id nodeID, User sender, Content content, Date date) {
        this(nodeName, nodeID, sender.getId(), content, date, MessageStatus.NOT_SENT);
    }

    /**
     * Constructor of a message from the JSON fields received from the server.
     * @param dateTime The date in String format.
     * @param senderID The is of the sender in String format.
     * @param text The text of the message (if it is a text message, null otherwise).
     * @param contentType The content type of the message.
     * @param content The content of the message.
     * @throws ParseException In case of parse exception with the date serialization.
     */
    public Message(String dateTime, Long senderID, String text, String contentType, String content)
            throws ParseException {
        super("message", new Id(DateSerialization.dateSerializer.toDate(Objects.requireNonNull(dateTime)
        ).getTime()));

        this.mSender = new Id(senderID);

        /*
        I WILL KILL YOU !!!!
        if (text != null){
            contentType = "text";
        }*/

        if (contentType.equals("image")){
            byte[] byteArray = Base64.decode(content, Base64.DEFAULT);
            Bitmap img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            if (img == null){
                Log.d("Y:"+ this.getClass().getName(), "Youston we have a problem");
                mContent = new TextContent(text);
            }else {
                mContent = new ImageContent(img, text);
            }
        }
        else {
            this.mContent = new TextContent(text);
        }

        this.mDate = DateSerialization.dateSerializer.toDate(dateTime);
        mStatus = MessageStatus.SENT;
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
        return YieldsApplication.getUser().getName() + " : " + mContent.getPreview();
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
     * Sets the MessageStatus of the message.
     *
     * @param messageStatus The MessageStatus of the message.
     */
    public void setStatus(MessageStatus messageStatus) {
        mStatus = messageStatus;
    }

    /**
     * Sets the MessageStatus of the message.
     *
     * @param messageStatus The MessageStatus of the message.
     */
    public void setStatus(MessageStatus messageStatus, Date timeStamp) {
        setStatus(messageStatus);
        mDate = timeStamp;
    }
}