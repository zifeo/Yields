package yields.client.messages;


import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

import yields.client.R;
import yields.client.exceptions.MessageException;
import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.yieldsapplication.YieldsApplication;


/**
 * Message is a {@code Node} which is shared as a message in a conversation.
 */
public class Message extends Node {

    public enum MessageStatus {
        RECEIVED("RECEIVED"), SEEN("SEEN"), NOT_SENT("NOT_SENT");

        private String mValue;

        MessageStatus(String value) {
            mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }

    private final User mSender;
    private final Content mContent;
    private final Date mDate;
    private final MessageStatus mStatus;

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
    public Message(String nodeName, Id nodeID, User sender, Content content,
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
        this(nodeName, nodeID, sender, content, date, MessageStatus.NOT_SENT);
    }

    /**
     * Create a message from a JSON object.
     *
     * @param object The JSON representing the message.
     * @throws JSONException if the json is invalid.
     */
<<<<<<< HEAD
    public Message(JSONArray object ) throws JSONException, ParseException{
        super(object.getString(0),
                new Id(DateSerialization.toDate(object.getString(0)).getTime()));
=======
    public Message(JSONObject object) throws JSONException {
        super(object.getString("datetime") + object.getString("user"),
                new Id(object.getString("id")));
>>>>>>> Add MessageStatus enum to Message, and use it as field, tests for this are also added.

        Id idUser = new Id(object.getString(1));
        /* TODO : For now the sender has its id as a name, we need to implement a request to do the mapping. */
        // TODO : The same apply for the profil pic and the email.
        User sender = new User(idUser.getId(), idUser, "",
                BitmapFactory.decodeResource(YieldsApplication.getApplicationContext().getResources(),
                        R.drawable.userpicture));

        this.mSender = sender;
        // TODO : Implement images !!!
        this.mContent = new TextContent(object.getString(3));
        //TODO : Implement Groups

        try {
            this.mDate = DateSerialization.toDate(object.getString(0));
        } catch (ParseException e) {
            throw new JSONException(e.getMessage());
        }

        mStatus = MessageStatus.NOT_SENT;
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
     * Returns a preview of the message, displayed in the group list.
     *
     * @return A string describing the message.
     */
    public String getPreview() {
        return mSender.getName() + " : " + mContent.getPreview();
    }

    /**
     * Returns the MessageStatus of the message.
     *
     * @return The MessageStatus of the message.
     */
    public MessageStatus getStatus() {
        return mStatus;
    }
}