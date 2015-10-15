package yields.client.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User extends Node{
    private static long nextId = 0;
    private String email;
    private List<Group> connectedGroups;

    private User(String name, long id,
                 String email, List<Group> groups) {

        super(name, id);
        this.connectedGroups = new ArrayList<>(groups);
        this.email = email;
    }

    private User(String name, String email, List<Group> groups) {

        this(name, nextId, email, groups);
        ++nextId;
    }

    public User(String name, String email) {
        this(name, email, new ArrayList<Group>());
    }

    public User(String name, long id, String email) {
        this(name, id, email, new ArrayList<Group>());
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void connectGroup(Group group) {
        connectedGroups.add(group);
    }

    public List<Group> getGroups() {
        return Collections.unmodifiableList(connectedGroups);
    }
}