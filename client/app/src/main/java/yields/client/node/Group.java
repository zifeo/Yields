package yields.client.node;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.yieldsapplication.YieldsApplication;

public class Group extends Node {

    public enum GroupVisibility {PRIVATE, PUBLIC};

    private TreeMap<Date, Message> mMessages;
    private boolean mValidated;
    private boolean mConsumed;
    private List<User> mUsers;
    private Bitmap mImage;
    private GroupVisibility mVisibility;


     /** Constructor for groups
     *
     * @param name The name of the group
     * @param id The id of the group
     * @param users The current users of the group
     * @param image The current image of the group
     * @param visibility The visibility of the group
	 * @param validated If the group has been validated by the server
     * @throws NodeException If nodes or image is null
     */
    public Group(String name, Id id, List<User> users, Bitmap image, GroupVisibility visibility, boolean validated) {
        super(name, id);
        Objects.requireNonNull(users);
        this.mMessages = new TreeMap<>();
        mConsumed = false;
        mUsers = new ArrayList<>(Objects.requireNonNull(users));
        mImage = Objects.requireNonNull(image);
        mValidated = validated;
        mVisibility = visibility;
    }

    /** Overloaded constructor for groups for default validation.
    * By default a group is not validated yet.
    * @param name The name of the group
    * @param id The id of the group
    * @param users The current users of the group
    * @param image The current image of the group
    * @param visibility The visibility of the group
    * @throws NodeException If nodes or image is null
            */
    public Group(String name, Id id, List<User> users, Bitmap image, GroupVisibility visibility) {
        this(name, id, users, image, visibility, false);
    }

    /** Overloaded constructor for groups for default visibility.
     * By default a group is set to private.
     * @param name The name of the group
     * @param id The id of the group
     * @param users The current users of the group
     * @param image The current image of the group
     * @throws NodeException If nodes or image is null
     */
    public Group(String name, Id id, List<User> users, Bitmap image) {
        this(name, id, users, image, GroupVisibility.PRIVATE, false);
    }

    /**
     * Overloaded constructor.
     * @param name The name of the group
     * @param id The id of the group
     * @param users The current users of the group
     * @throws NodeException if one of the node is null.
     */
    public Group(String name, Id id, List<User> users) {
        this(name, id, users, YieldsApplication.getDefaultGroupImage(), GroupVisibility.PRIVATE, false);
    }

    /**
     * Set the visibility of a group.
     * @param visibility The new visibility.
     */
    public void setVisibility(GroupVisibility visibility){
        mVisibility = visibility;
    }

    /**
     * Getter for the group visibility.
     * @return The visibility attribute of the group.
     */
    public GroupVisibility getVisibility(){
        return mVisibility;
    }

    /**
     * Add a user to the group
     * @param user The user we want to add
     */
    public void addUser(User user){
        Objects.requireNonNull(user);
        mUsers.add(user);
    }

    /**
     * Indicates if a user belongs to the group
     * @param user The user we want to test
     */
    public boolean containsUser(User user){
        Objects.requireNonNull(user);
        return mUsers.contains(user);
    }

    /**
     * Add a new message to the group messages.
     * @param newMessage if the message is not valid.
     */
    public void addMessage(Message newMessage) {
        mMessages.put(newMessage.getDate(), newMessage);
    }


    /**
     * Set the image to the group
     * @param image A squared image which this method will make circular
     */
    public void setImage(Bitmap image){
        mImage = Objects.requireNonNull(image);
    }

    /**
     * Returns the group's image
     * @return the group's image, uncropped
     */
    public Bitmap getImage(){
        return mImage;
    }

    /**
     * Indicate that the server has validated
     * the created group
     */
    public void setValidated(){
        mValidated = true;
    }

    /**
     *  Indicate if the group has been validated by the server
     * @return true iff the group has been validated by the server
     */
    public boolean isValidated(){
        return mValidated;
    }

    /**
     *  Returns the lasts messages of the group (up to a certain date util the user scrolls up).
     * @return A sorted map containing the messages sorted by date.
     * @throws IOException In case the user cannot retreive the messages.
     */
    public SortedMap<Date, Message> getLastMessages() throws IOException{
        Map.Entry<Date, Message> message = mMessages.firstEntry();

        Date farthestDate;

        if (message == null) {
            farthestDate = new Date();
        } else {
            farthestDate = message.getKey();
        }

        List<Message> messages = YieldsApplication.getUser()
                .getGroupMessages(this, farthestDate);

        for(Message m : messages){
            mMessages.put(m.getDate(),m);
        }

        return Collections.unmodifiableSortedMap(mMessages
                .tailMap(farthestDate));
    }

    /**
     * Return the very last message.
     * @return The last message.
     */
    public Message getLastMessage(){
        return mMessages.firstEntry().getValue();
    }

    /**
     * Returns the users of the group.
     * @return the users.
     */
    public List<User> getUsers() {
        return Collections.unmodifiableList(mUsers);
    }

    /**
     * Returns the preview of the last message posted on this group
     * @return the preview of the last message or "" if there are no messages
     */
    public String getPreviewOfLastMessage(){
        if (mMessages.size() > 0){
            return getLastMessage().getPreview();
        }
        else{
            return "";
        }
    }
}