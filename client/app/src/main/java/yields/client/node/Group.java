package yields.client.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;

public class Group extends Node {
    private List<User> connectedUsers;

    public Group(String name, Id id, List<User> connectedUsers){
        super(name, id);
        Objects.requireNonNull(connectedUsers);
        this.connectedUsers = new ArrayList<>(connectedUsers);
    }

    private void connectUser(User user) throws NodeException {
        User newUser = new User(user.getName(), user.getId(),
                user.getEmail(), user.getImg());
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