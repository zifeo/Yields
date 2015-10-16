package yields.client.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Group extends Node {
    private List<User> connectedUsers;

    public Group(String name, long id, List<User> connectedUsers) {
        super(name, id);
        this.connectedUsers = new ArrayList<>(connectedUsers);
    }

    private void connectUser(User user) {
        User newUser = new User(user.getName(), user.getId(),
                user.getEmail());

        connectedUsers.add(newUser);
    }

    public void appendUsers(Collection<User> users) {
        for (User user: users ) {
            connectUser(user);
        }
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(connectedUsers);
    }
}