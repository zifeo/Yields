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

    public enum GroupType {
        PRIVATE("PRIVATE"), PUBLISHER("PUBLISHER"), RSS("RSS");

        private final String mName;

        GroupType(String name) {
            mName = name;
        }

        public String getValue() {
            return mName;
        }
    }

    private TreeMap<Date, Message> mMessages;
    private boolean mValidated;
    private List<User> mUsers;
    private List<Group> mNodes;
    private GroupType mType;
    private Set<Tag> mTags;
    private Date mDate;

    /**
     * Constructor for groups
     *
     * @param name      The name of the group
     * @param id        The id of the group
     * @param users     The current users of the group
     * @param image     The current image of the group
     * @param type      The type of the group
     * @param validated If the group has been validated by the server
     * @throws NodeException If nodes or image is null
     */
    public Group(String name, Id id, List<Id> users, Bitmap image, GroupType type,
                 boolean validated, Date lastUpdate) {
        super(name, id, image);
        mDate = Objects.requireNonNull(lastUpdate);
        mMessages = new TreeMap<>();
        mValidated = validated;
        mType = type;
        mTags = new HashSet<>();
        mDate = lastUpdate;
        mNodes = new ArrayList<>();

        Objects.requireNonNull(users);
        mUsers = new ArrayList<>();
        for (Id uId : users) {
            mUsers.add(YieldsApplication.getUserFromId(uId));
        }
    }

    /**
     * Constructor for groups
     *
     * @param name The name of the group
     * @param id   The id of the group
     * @throws NodeException If nodes or image is null
     */
    public Group(String name, Id id, Group group) {
        super(name, id, group.getImage());
        Objects.requireNonNull(group);
        this.mMessages = new TreeMap<>();
        mUsers = group.getUsers();
        mValidated = false;
        mType = GroupType.PRIVATE;
        mTags = new HashSet<>();
        mDate = new Date();
        mNodes = new ArrayList<>();
    }

    /**
     * Overloaded constructor for groups for default type.
     * By default a group is set to private.
     *
     * @param name  The name of the group
     * @param id    The id of the group
     * @param users The current users of the group
     * @param image The current image of the group
     * @throws NodeException If nodes or image is null
     */
    public Group(String name, Id id, List<Id> users, Bitmap image) {
        this(name, id, users, image, GroupType.PRIVATE, false, new Date());
    }

    /**
     * Overloaded constructor.
     *
     * @param name  The name of the group
     * @param id    The id of the group
     * @param users The current users of the group
     * @throws NodeException if one of the node is null.
     */
    public Group(String name, Id id, List<Id> users) {
        this(name, id, users, YieldsApplication.getDefaultGroupImage(), GroupType.PRIVATE,
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
    public Group(String name, Id id, List<Id> users, Id node) {
        this(name, id, users, YieldsApplication.getDefaultGroupImage(), GroupType.PRIVATE,
                false, new Date());
        this.addNode(YieldsApplication.getUser().getNodeFromId(node));
    }

    /**
     * Overloaded constructor.
     *
     * @param name  The name of the group
     * @param id    The id of the group
     * @param users The current users of the group
     * @throws NodeException if one of the node is null.
     */
    public Group(String name, Id id, List<Id> users, Boolean validated, Date lastUpdate) {
        this(name, id, users, YieldsApplication.getDefaultGroupImage(), GroupType.PRIVATE,
                validated, lastUpdate);
    }

    /**
     * Creates a group from a Json response coming from the server.
     *
     * @param jsonGroup The JsonArray containing the group.
     * @throws JSONException
     */
    public Group(JSONArray jsonGroup) throws JSONException, ParseException {
        this(jsonGroup.getString(1), new Id(jsonGroup.getLong(0)), new ArrayList<Id>(), true,
                DateSerialization.dateSerializer.toDate(jsonGroup.getString(2)));
    }

    /**
     * Construct a Group from the Id and the name recieved from the server.
     *
     * @param groupId     The Id of the group in String format.
     * @param name        The name of the group.
     * @param refreshedAt Last date the group has been refreshed.
     */
    public Group(String groupId, String name, String refreshedAt) throws ParseException {
        this(name, new Id(Long.parseLong(groupId)), new ArrayList<Id>(), true,
                DateSerialization.dateSerializer.toDate(refreshedAt));
    }

    /**
     * Get the last time the group was updated.
     *
     * @return The sus-mentioned date.
     */
    public Date getLastUpdate() {
        return new Date(mDate.getTime());
    }

    /**
     * Set's the last time the group was updated.
     *
     * @param date The date of the last update.
     */
    public void setLastUpdate(Date date){
        if (date.compareTo(mDate) > 0) {
            mDate = new Date(date.getTime());
        }
    }

    /**
     * Create a wrapper for the comment message in order to be able to send and retreive comment
     * for this message.
     *
     * @param messageComment The message commented.
     * @param group          The parent group containing the message.
     * @return The group wrapper for this message.
     */
    public static Group createGroupForMessageComment(Message messageComment, Group group) {
        return new Group("message comment", messageComment.getCommentGroupId(), group);
    }

    /**
     * Validates a message received at a certain date and changes the date to the server side date.
     * The message also sees it's Id updated if acts as a Node on the server side.
     * The message status is always set to SENT.
     *
     * @param contentId The Id of the Message if there is one.
     * @param date      The old Date of the Message.
     * @param newDate   The newDate of the Message.
     * @return The Message updated.
     */
    public Message updateMessageIdDateAndStatus(Id contentId, Date date, Date newDate) {
        Integer size = this.mMessages.size();
        Log.d("Y:" + this.getClass().getName(), size.toString());

        Message message = mMessages.remove(date);

        size = this.mMessages.size();
        Log.d("Y:" + this.getClass().getName(), size.toString());

        if (message != null) {
            message.setStatusAndUpdateDate(Message.MessageStatus.SENT, newDate);
            message.setCommentGroupId(contentId);
            message.recomputeView();
            mMessages.put(newDate, message);
            return message;
        } else {
            Log.d("Y:" + this.getClass().getName(), DateSerialization.dateSerializer.toString(date));
            Log.d("Y:" + this.getClass().getName(), "Couldn't update message " + contentId.getId().toString()
                    + " as it was not in the group with id " + this.getId().getId().toString());
            return null;
        }
    }

    /**
     * Set the type of a group.
     *
     * @param type The new type.
     */
    public void setType(GroupType type) {
        mType = type;
    }

    /**
     * Getter for the group type.
     *
     * @return The type attribute of the group.
     */
    public GroupType getType() {
        return mType;
    }

    /**
     * Add a user to the group
     *
     * @param user The user we want to add
     */
    public void addUser(Id user) {
        Objects.requireNonNull(user);
        mUsers.add(YieldsApplication.getUserFromId(user));
    }
    /**
     * Remove a user from the group.
     *
     * @param user The user we want to remove.
     */
    public void removeUser(Id user) {
        Objects.requireNonNull(user);
        mUsers.remove(YieldsApplication.getUserFromId(user));
    }


    /**
     * Changes/updates the users of the group
     *
     * @param userList The new list of Users for the group.
     */
    public void updateUsers(ArrayList<Id> userList) {
        mUsers.clear();
        for (Id uId : userList) {
            mUsers.add(YieldsApplication.getUserFromId(uId));
        }
    }

    /**
     * Changes/updates the users of the group
     *
     * @param nodeList
     */
    public void updateNodes(ArrayList<Id> nodeList) {
        mNodes.clear();
        for (Id uId : nodeList) {
            mNodes.add(YieldsApplication.getUser().getNodeFromId(uId));
        }
    }

    /**
     * Gets all nodes from a group.
     *
     * @return A list of group nodes.
     */
    public List<Group> getNodes() {
        return mNodes;
    }

    /**
     * Adds a group node to the nodes of the group.
     *
     * @param node The group node to add.
     */
    public void addNode(Group node) {
        mNodes.add(Objects.requireNonNull(node));
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
     * Indicates if a node belongs to the group
     *
     * @param group The Group we want to test
     */
    public boolean containsNode(Group group) {
        Objects.requireNonNull(group);
        return mNodes.contains(group);
    }

    /**
     * Add a new message to the group messages.
     *
     * @param newMessage Message to be added
     */
    synchronized public void addMessage(Message newMessage) {
        Message previous = mMessages.put(newMessage.getDate(), newMessage);
        this.setLastUpdate(newMessage.getDate());
        String previousMessage;
        if (previous == null) {
            previousMessage = "null";
        } else {
            previousMessage = previous.getContent().getTextForRequest();
        }
        Integer size = mMessages.size();
        Log.d("Y:" + this.getClass().toString(), "New message count in Group is : " + size.toString());
        Log.d("Y:" + this.getClass().toString(), DateSerialization.dateSerializer.toString(newMessage.getDate()));

        Log.d("Y:" + this.getClass().toString(), "Added message to group: " + this.getId().getId().toString()
                + ", this message was replaced: " + previousMessage);
        Log.d("Y:" + this.getClass().toString(), "Message added had text " + newMessage.getContent().getTextForRequest());
    }

    /**
     * Add a new message to the group messages.
     *
     * @param newMessageList Messages to be added.
     */
    synchronized public void addMessages(List<Message> newMessageList) {
        for (Message newMessage : newMessageList) {
            addMessage(newMessage);
        }

        if (!mMessages.isEmpty()) {
            setLastUpdate(mMessages.lastKey());
        }
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