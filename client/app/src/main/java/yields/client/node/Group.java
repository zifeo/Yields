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

public class Group extends Node {
    private List<User> mUsers; // for now, just users
    private Bitmap mImage;
    private List<Message> mCurrentMessages; // not all the messages the group has ever sent, just
                                            // the ones that are currently in the listView



     /** Constructor for groups
     *
     * @param name The name of the group
     * @param id The id of the group
     * @param nodes The current nodes of the group
     * @param image The current image of the group, can be null
     * @param isImageCircle Indicates if the image given is already circular
     * @throws NodeException If nodes is null
     */
    public Group(String name, Id id, List<User> users, Bitmap image, boolean isImageCircle) throws NodeException {
        super(name, id);
        mUsers = new ArrayList<>(Objects.requireNonNull(users));
        mCurrentMessages = new ArrayList<>();

        if (image != null && !isImageCircle){
            mImage = GraphicTransforms.getCroppedCircleBitmap(image, R.integer.groupImageDiameter);
        }
    }

    public Group(String name, Id id, List<User> users, Bitmap image) throws NodeException {
        this(name, id, users, image, false);
    }

    public Group(String name, Id id, List<User> users) throws NodeException {
        this(name, id, users, null, false);
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
     * Returns the circular version of the group's image
     * @return the circular version of the group's image, or null if none has been set
     */
    public Bitmap getCircularImage(){
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