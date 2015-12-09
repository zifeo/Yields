package yields.client.node;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.Objects;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;

/**
 * Represent a node in the model of our application.
 */
public abstract class Node {
    private String mName;
    private Id mId;
    private Bitmap mImage;

    /**
     * Creates a node.
     *
     * @param name The name of the node.
     * @param id The Id of the node.
     * @throws NodeException In case of trouble making the node.
     */
    public Node(String name, Id id, Bitmap image) throws NodeException {
        this.mName = Objects.requireNonNull(name);
        this.mId = Objects.requireNonNull(id);
        this.mImage = Objects.requireNonNull(image);
    }

    /**
     * Gets the name of the node.
     *
     * @return The name of the node.
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the name of the node.
     *
     * @param name The name of the node.
     */
    public void setName(String name){
        mName = Objects.requireNonNull(name);
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
     * Get's the id of the node.
     *
     * @return The id of the node.
     */
    public Id getId() {
        return mId;
    }

    /**
     * Set's the id of the node.
     *
     * @param id The new id of the node.
     */
    public void setId(Id id) {
        mId = id;
    }

}
