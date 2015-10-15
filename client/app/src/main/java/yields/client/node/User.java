package yields.client.node;

public class User extends Node{
    private static long ids = 0;
    private String email;

    private User(String name) {
        super(name, ids);
        ++ids;
    }

    public User(String name, String email) {
        this(name);
        this.email = email;
    }

    public User(String name, long id, String email) {
        super(name, id);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void connectGroup(Group group) {
        connectNode(group);
    }
}