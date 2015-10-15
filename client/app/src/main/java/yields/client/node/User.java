package yields.client.node;

public class User extends Node{
    String email;

    public User(String name, long id, String email) {
        super(name, id);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
