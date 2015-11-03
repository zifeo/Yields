package yields.client.node;


import android.util.Log;

import java.io.IOException;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import yields.client.R;
import yields.client.exceptions.NodeException;
import yields.client.gui.GraphicTransforms;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.yieldsapplication.YieldsApplication;

public class Group extends Node {
    private List<User> mConnectedUsers;
    private TreeMap<Date, Message> mMessages;
    private boolean mConsumed;

    private List<User> mUsers;
    private Bitmap mImage;
    private List<Message> mCurrentMessages; // not all the messages the group has ever sent, just
                                            // the ones that are currently in the listView


     /** Constructor for groups
     *
     * @param name The name of the group
     * @param id The id of the group
     * @param users The current users of the group
     * @param image The current image of the group
     * @throws NodeException If nodes or image is null
     */
    public Group(String name, Id id, List<User> users, Bitmap image)
            throws NodeException {
        super(name, id);
        Objects.requireNonNull(users);
        this.mConnectedUsers = new ArrayList<>(users);
        this.mMessages = new TreeMap<>();
        mConsumed = false;
        mUsers = new ArrayList<>(Objects.requireNonNull(users));
        mCurrentMessages = new ArrayList<>();
        mImage = Objects.requireNonNull(image);

    }

    public Group(String name, Id id, List<User> users) throws NodeException {
        this(name, id, users, YieldsApplication.getDefaultGroupImage());
    }

    private void connectUser(User user) throws NodeException {
        User newUser = new User(user.getName(), user.getId(),
                user.getEmail(), user.getImg());
        mConnectedUsers.add(newUser);
    }

    public void appendUsers(Collection<User> users) throws NodeException {
        for (User user : users) {
            connectUser(user);
        }
    }


    public void addUser(User newUser) {
        // TODO : Check if node is not a message

        mUsers.add(newUser);
    }

    public void addMessage(Message newMessage) {
        mMessages.put(newMessage.getDate(), newMessage);
    }

    public SortedMap<Date, Message> getCurrentMessages(){
        return Collections.unmodifiableSortedMap(mMessages);
    }

    /**
     * Set the image to the group
     * @param image A squared image which this method will make circular
     */
    public void setImage(Bitmap image){
        Objects.requireNonNull(image);
        mImage = GraphicTransforms.getCroppedCircleBitmap(image, R.integer.groupImageDiameter);
    }

    /**
     * Returns the group's image
     * @return the group's image, uncropped
     */
    public Bitmap getImage(){
        return mImage;
    }

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

    public Message getLastMessage(){
        return mMessages.firstEntry().getValue();
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(mUsers);
    }

    /**
     * Returns the preview of the last message posted on this group
     * @return the preview of the last message or "" if there are no messages
     */
    public String getPreviewOfLastMessage(){
        if (mCurrentMessages.size() > 0){
            return getLastMessage().getPreview();
        }
        else {
            return "";
        }
    }
}