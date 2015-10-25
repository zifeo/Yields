package yields.client.node;

import android.graphics.Bitmap;

import java.util.Objects;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;

public class User extends Node{

    private String email;
    private Bitmap mImg;

    public User(String name, Id id,
                 String email, Bitmap img) throws NodeException {
        super(name, id);
        Objects.requireNonNull(email);
        Objects.requireNonNull(img);
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