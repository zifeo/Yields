package yields.client.node;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;

public abstract class Node {
    private final String mName;
    private final Id mId;

    public Node(String name, Id id) throws NodeException {
        this.mName = name;
        if (id == null){
            throw new NodeException("Error, null id in Node constructor");
        }
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public Id getId() {
        return mId;
    }

}