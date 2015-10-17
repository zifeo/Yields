package yields.client.node;

import yields.client.id.Id;

public class User extends Node{
    private String email;

    public User(String name, Id id,
                 String email) {

        super(name, id);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}