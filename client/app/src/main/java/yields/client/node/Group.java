package yields.client.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Group extends Node {
    public static long ids = 0;
    private List<User> connectedNodes;

    public Group(String name, List<User> connectedNodes) {
        super(name, ids);
        ++ids;
        this.connectedNodes = connectedNodes;
    }

    public Group(String name) {
        this(name, new ArrayList<User>());
    }

    public void connectUser(User user) {
        connectedNodes.add(user);
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(connectedNodes);
    }
}