package yields.client.node;

import java.util.Objects;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;

public abstract class Node {
    private final String mName;
    private final Id mId;

    public Node(String name, Id id) throws NodeException {
        this.mName = Objects.requireNonNull(name);
        this.mId = Objects.requireNonNull(id);
    }

    public String getName() {
        return mName;
    }

    public Id getId() {
        return mId;
    }

}
