package yields.client.node;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.exceptions.NodeException;
import yields.client.gui.GraphicTransforms;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.yieldsapplication.YieldsApplication;

public class Group extends Node {
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
        mUsers = new ArrayList<>(Objects.requireNonNull(users));
        mCurrentMessages = new ArrayList<>();
        mImage = Objects.requireNonNull(image);
    }

    public Group(String name, Id id, List<User> users) throws NodeException {
        this(name, id, users, YieldsApplication.getDefaultGroupImage());
    }

    public void addUser(User newUser) {
        // TODO : Check if node is not a message

        mUsers.add(newUser);
    }

    public void addMessage(Message newMessage) {
        mCurrentMessages.add(newMessage);
    }

    public List<Message> getCurrentMessages(){
        return Collections.unmodifiableList(mCurrentMessages);
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

    public List<User> getUsers() {
        return Collections.unmodifiableList(mUsers);
    }

    /**
     * Returns the preview of the last message posted on this group
     * @return the preview of the last message or "" if there are no messages
     */
    public String getPreviewOfLastMessage(){
        if (mCurrentMessages.size() > 0){
            return mCurrentMessages.get(mCurrentMessages.size() - 1).getPreview();
        }
        else {
            return "";
        }
    }
}