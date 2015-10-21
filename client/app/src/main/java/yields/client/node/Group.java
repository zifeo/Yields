package yields.client.node;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.Message;

public class Group extends Node {
    private List<Node> mNodes; // for now, just users
    private Bitmap mImage;
    private List<Message> mCurrentMessages; // not all the messages the group has ever sent, just
                                            // the ones that are currently in the listView
    

    /** Constructor for groups
     *
     * @param name The name of the group
     * @param id The id of the group
     * @param nodes The current nodes of the group
     * @param image The current image of the group, can be null
     * @throws NodeException If nodes is null
     */
    public Group(String name, Id id, List<Node> nodes, Bitmap image) throws NodeException {
        super(name, id);
        if (nodes == null){
            throw new NodeException("Error, null list in Group constructor");
        }
        mNodes = new ArrayList<>(nodes);
        mImage = image;
    }

    public Group(String name, Id id, List<Node> nodes) throws NodeException {
        this(name, id, nodes, null);
    }

    public void addNode(Node newNode) {
        // TODO : Check if node is not a message

        mNodes.add(newNode);
    }

    public void addMessage(Message newMessage) {
        mCurrentMessages.add(newMessage);
    }

    public void setImage(Bitmap image){
        if (image == null){
            throw new IllegalArgumentException("Null image in setImage");
        }

        mImage = image;
    }

    public List<Node> getUsers() {
        return Collections.unmodifiableList(mNodes);
    }
}