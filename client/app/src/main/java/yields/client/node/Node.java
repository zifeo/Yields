package yields.client.node;

import java.util.Objects;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;

public abstract class Node {
    private final String mName;
    private final Id mId;

    public Node(String name, Id id) throws NodeException {
        Objects.requireNonNull(name);
        Objects.requireNonNull(id);
        this.mName = name;
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public Id getId() {
        return mId;
    }

}
