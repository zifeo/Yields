package yields.client.node;

public class Group extends Node {
    public static long ids = 0;

    public Group(String name) {
        super(name, ids);
        ++ids;
    }

    public void connectUser(User user) {
        connectNode(user);
    }
}