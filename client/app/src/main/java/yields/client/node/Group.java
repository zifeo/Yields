package yields.client.node;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.serverconnection.DateSerialization;
import yields.client.yieldsapplication.YieldsApplication;

public class Group extends Node {

    public enum GroupVisibility {
        PRIVATE("PRIVATE"), PUBLIC("PUBLIC");

        private final String mName;

        GroupVisibility(String name) {
            mName = name;
        }

        public String getValue() {
            return mName;
        }
    }

    private TreeMap<Date, Message> mMessages;
    private boolean mValidated;
    private List<User> mUsers;
    private Bitmap mImage;
    private GroupVisibility mVisibility;
    private Set<Tag> mTags;
    private Date mDate;

    /**
     * Constructor for groups
     *
     * @param name       The name of the group
     * @param id         The id of the group
     * @param users      The current users of the group
     * @param image      The current image of the group
     * @param visibility The visibility of the group
     * @param validated  If the group has been validated by the server
     * @throws NodeException If nodes or image is null
     */
    public Group(String name, Id id, List<User> users, Bitmap image, GroupVisibility visibility,
                 boolean validated, Date lastUpdate) {
        super(name, id);
        Objects.requireNonNull(users);
        Objects.requireNonNull(lastUpdate);
        this.mMessages = new TreeMap<>();
        mUsers = new ArrayList<>(Objects.requireNonNull(users));
        mImage = Objects.requireNonNull(image);
        mValidated = validated;
        mVisibility = visibility;
        mTags = new HashSet<>();
        mDate = lastUpdate;
    }

    /**
     * Overloaded constructor for groups for default validation.
     * By default a group is not validated yet.
     *
     * @param name       The name of the group
     * @param id         The id of the group
     * @param users      The current users of the group
     * @param image      The current image of the group
     * @param visibility The visibility of the group
     * @throws NodeException If nodes or image is null
     */
    public Group(String name, Id id, List<User> users, Bitmap image, GroupVisibility visibility) {
        this(name, id, users, image, visibility, false, new Date());
    }

    /**
     * Overloaded constructor for groups for default visibility.
     * By default a group is set to private.
     *
     * @param name  The name of the group
     * @param id    The id of the group
     * @param users The current users of the group
     * @param image The current image of the group
     * @throws NodeException If nodes or image is null
     */
    public Group(String name, Id id, List<User> users, Bitmap image) {
        this(name, id, users, image, GroupVisibility.PRIVATE, false, new Date());
    }

    /**
     * Overloaded constructor.
     *
     * @param name  The name of the group
     * @param id    The id of the group
     * @param users The current users of the group
     * @throws NodeException if one of the node is null.
     */
    public Group(String name, Id id, List<User> users) {
        this(name, id, users, YieldsApplication.getDefaultGroupImage(), GroupVisibility.PRIVATE,
                false, new Date());
    }

    /**
     * Overloaded constructor.
     *
     * @param name  The name of the group
     * @param id    The id of the group
     * @param users The current users of the group
     * @throws NodeException if one of the node is null.
     */
    public Group(String name, Id id, List<User> users, Boolean validated, Date lastUpdate) {
        this(name, id, users, YieldsApplication.getDefaultGroupImage(), GroupVisibility.PRIVATE,
                validated, lastUpdate);
    }

    /**
     * Creates a group from a Json response coming from the server.
     *
     * @param jsonGroup The JsonArray containing the group.
     * @throws JSONException
     */
    public Group(JSONArray jsonGroup) throws JSONException, ParseException{
        this(jsonGroup.getString(1), new Id(jsonGroup.getLong(0)), new ArrayList<User>(), false,
                DateSerialization.dateSerializer.toDate(jsonGroup.getString(2)));
    }

    /**
     * Construct a Group from the Id and the name recieved from the server.
     * @param groupId The Id of the group in String format.
     * @param name The name of the group.
     * @param refreshedAt Last date the group has been refreshed.
     */
    public Group(String groupId, String name, String refreshedAt) throws ParseException {
        this(name, new Id(Long.parseLong(groupId)), new ArrayList<User>(), true,
                DateSerialization.dateSerializer.toDate(refreshedAt));
    }

    /**
     * Get the last time the group was updated.
     * @return The sus-mentioned date.
     */
    public Date getLastUpdate(){
        return new Date(mDate.getTime());
    }

    /**
     * Set's the last time the group was updated.
     * @param date The date of the last update.
     */
    public void setLastUpdate(Date date){
        mDate = new Date(date.getTime());
    }

    /**
     * Create a wrapper for the comment message in order to be able to send and retreive comment
     * for this message.
     * @param messageComment The message commented.
     * @param group The parent group containing the message.
     * @return The group wrapper for this message.
     */
    public static Group createGroupForMessageComment(Message messageComment, Group group) {
        return new Group("message comment", messageComment.getId(), group.getUsers());
    }

    /**
     *
     */
    public void validateMessage(Date date, Date newDate){
        Message message = mMessages.remove(date);
        if (message != null){
            message.setStatus(Message.MessageStatus.SENT, newDate);
            mMessages.put(newDate, message);
        } else {
            Log.d("Y:" + this.getClass().getName(), mMessages.keySet().toString());
        }
    }

    /**
     * Set the visibility of a group.
     *
     * @param visibility The new visibility.
     */
    public void setVisibility(GroupVisibility visibility) {
        mVisibility = visibility;
    }

    /**
     * Getter for the group visibility.
     *
     * @return The visibility attribute of the group.
     */
    public GroupVisibility getVisibility() {
        return mVisibility;
    }

    /**
     * Add a user to the group
     *
     * @param user The user we want to add
     */
    public void addUser(User user) {
        Objects.requireNonNull(user);
        mUsers.add(user);
    }

    /**
     * Indicates if a user belongs to the group
     *
     * @param user The user we want to test
     */
    public boolean containsUser(User user) {
        Objects.requireNonNull(user);
        return mUsers.contains(user);
    }

    /**
     * Add a new message to the group messages.
     *
     * @param newMessage Message to be added
     */
    synchronized public void addMessage(Message newMessage) {
        mMessages.put(newMessage.getDate(), newMessage);
    }

    /**
     * Add a new message to the group messages.
     *
     * @param newMessageList Messages to be added.
     */
    synchronized public void addMessages(List<Message> newMessageList) {
        for (Message newMessage : newMessageList) {
            mMessages.put(newMessage.getDate(), newMessage);
        }
    }


    /**
     * Set the image to the group
     *
     * @param image A squared image which this method will make circular
     */
    public void setImage(Bitmap image) {
        mImage = Objects.requireNonNull(image);
    }

    /**
     * Returns the group's image
     *
     * @return the group's image, uncropped
     */
    public Bitmap getImage() {
        return mImage;
    }

    /**
     * Add a tag to the group
     *
     * @param tag The tag we want to add, without spaces, in lowercase
     */
    public void addTag(Tag tag) {
        mTags.add(Objects.requireNonNull(tag));
    }

    /**
     * Indicate whether the group can be matched with a given tag
     *
     * @param tag The tag we want to test
     * @return true iff the group contains the given tag
     */
    public boolean matchToTag(Tag tag) {
        return mTags.contains(tag);
    }

    /**
     * Returns a list containing all the tags of the group
     *
     * @return A list containing all the tags of the group, in a random order
     */
    public List<Tag> getTagList() {
        return new ArrayList<>(mTags);
    }

    /**
     * Indicate that the server has validated
     * the created group
     */
    public void setValidated() {
        mValidated = true;
    }

    /**
     * Indicate if the group has been validated by the server
     *
     * @return true iff the group has been validated by the server
     */
    public boolean isValidated() {
        return mValidated;
    }

    /**
     * Returns the lasts messages of the group (up to a certain date util the user scrolls up).
     *
     * @return A sorted map containing the messages sorted by date.
     * @throws IOException In case the user cannot retreive the messages.
     */
    synchronized public SortedMap<Date, Message> getLastMessages() {
        return Collections.unmodifiableSortedMap(mMessages);
    }

    /**
     * Returns the users of the group.
     *
     * @return the users.
     */
    public List<User> getUsers() {
        return Collections.unmodifiableList(mUsers);
    }

    /**
     * Returns the preview of the last message posted on this group
     *
     * @return the preview of the last message or "" if there are no messages
     */
    synchronized public String getPreviewOfLastMessage() {
        return mMessages.size() > 0 ? getLastMessage().getPreview() : "";
    }

    /**
     * Return the very last message.
     *
     * @return The last message.
     */
    private Message getLastMessage() {
        return mMessages.lastEntry().getValue();
    }

    /**
     * Class used to represent a tag, which defines the subjects of the group
     */
    public static class Tag {

        private String mText;
        public static final int MIN_TAG_LENGTH = 2;
        public static final int MAX_TAG_LENGTH = 20;

        /**
         * Default constructor for tags
         *
         * @param text The text of the tag
         */
        public Tag(String text) {
            if (text.contains(" ")) {
                throw new IllegalArgumentException("Tag cannot contain spaces");
            }
            if (!text.toLowerCase().equals(text)) {
                throw new IllegalArgumentException("Tag must be in lowercase");
            }
            if (text.length() < MIN_TAG_LENGTH) {
                throw new IllegalArgumentException(
                        "Length of a Tag must be at least " + MIN_TAG_LENGTH + " characters");
            }
            if (text.length() > MAX_TAG_LENGTH) {
                throw new IllegalArgumentException(
                        "Length of a Tag cannot be more than " + MAX_TAG_LENGTH + " characters");
            }

            mText = Objects.requireNonNull(text);
        }

        /**
         * Compares a tag and another object
         *
         * @param o The object we want to test
         * @return true iff the two objects are equals
         */
        @Override
        public boolean equals(Object o) {
            if (o instanceof Tag) {
                return ((Tag) o).mText.equals(this.mText);
            }
            return super.equals(o);
        }

        /**
         * Gives the hashcode of a tag
         *
         * @return The hash of this tag
         */
        @Override
        public int hashCode() {
            return mText.hashCode();
        }

        /**
         * Returns the text of the tag
         *
         * @return The text of the tag
         */
        public String getText() {
            return mText;
        }
    }
}