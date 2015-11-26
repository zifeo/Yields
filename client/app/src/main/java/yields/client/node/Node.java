package yields.client.node;

import java.util.Objects;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;

/**
 * Represent a node in the model of our application.
 */
public abstract class Node {
    private String mName;
    private Id mId;

    /**
     * Creates a node.
     *
     * @param name The name of the node.
     * @param id The Id of the node.
     * @throws NodeException In case of trouble making the node.
     */
    public Node(String name, Id id) throws NodeException {
        this.mName = Objects.requireNonNull(name);
        this.mId = Objects.requireNonNull(id);
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
