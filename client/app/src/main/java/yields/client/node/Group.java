package yields.client.node;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

    private List<User> mConnectedUsers;
    private TreeMap<Date, Message> mMessages;
    private boolean mConsumed;
    private List<User> mUsers;
    private Bitmap mImage;


     /** Constructor for groups
     *
     * @param name The name of the group
     * @param id The id of the group
     * @param users The current users of the group
     * @param image The current image of the group
     * @throws NodeException If nodes or image is null
     */
    public Group(String name, Id id, List<User> users, Bitmap image) {
        super(name, id);
        Objects.requireNonNull(users);
        this.mConnectedUsers = new ArrayList<>(users);
        this.mMessages = new TreeMap<>();
        mConsumed = false;
        mUsers = new ArrayList<>(Objects.requireNonNull(users));
        mImage = Objects.requireNonNull(image);
    }

    /**
     * Overloaded constructor for default image.
     * @param name The name of the group
     * @param id The id of the group
     * @param users The current users of the group
     * @throws NodeException if one of the node is null.
     */
    public Group(String name, Id id, List<User> users) {
        this(name, id, users, YieldsApplication.getDefaultGroupImage());
    }

    /**
     * Add a user to a group.
     * @param user the user to add.
     * @throws NodeException if the user in not valid.
     */
    private void connectUser(User user) throws NodeException {
        User newUser = new User(user.getName(), user.getId(),
                user.getEmail(), user.getImg());
        mConnectedUsers.add(newUser);
    }

    /**
     * Add a list of users to the group.
     * @param users The list of users to add.
     * @throws NodeException if the list is invalid.
     */
    public void appendUsers(Collection<User> users) throws NodeException {
        for (User user : users) {
            connectUser(user);
        }
    }

    /**
     * Add e new message to the group messages.
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