package yields.client.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;

public class Group extends Node {
    private List<User> connectedUsers;

    public Group(String name, Id id, List<User> connectedUsers) throws NodeException {
        super(name, id);
        if (connectedUsers == null){
            throw new NodeException("Error, null list in Group constructor");
        }
        this.connectedUsers = new ArrayList<>(connectedUsers);
    }

    private void connectUser(User user) throws NodeException {
        User newUser = new User(user.getName(), user.getId(),
                user.getEmail());

        connectedUsers.add(newUser);
    }

    public void appendUsers(Collection<User> users) throws NodeException {
        for (User user: users ) {
            connectUser(user);
        }
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(connectedUsers);
    }
}