package yields.client.node;

import android.graphics.Bitmap;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;

public class User extends Node{

    private String email;
    private Bitmap mImg;

    public User(String name, Id id,
                 String email, Bitmap img) throws NodeException {
        super(name, id);
        this.email = email;
        this.mImg = img;
    }

    public Bitmap getImg(){
        return mImg;
    }

    public String getEmail() {
        return email;
    }
}