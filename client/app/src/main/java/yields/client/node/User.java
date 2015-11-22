package yields.client.node;

import android.graphics.Bitmap;

import java.util.Objects;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;

public class User extends Node {

    private String mEmail;
    private Bitmap mImg;

    /**
     * Constructor for the User class.
     *
     * @param name  Name of the user.
     * @param id    Id of the user.
     * @param email Email of the user.
     * @param img   Image of the user.
     * @throws NodeException If any of those parameters are invalid.
     */
    public User(String name, Id id,
                String email, Bitmap img) throws NodeException {
        super(name, id);
        this.mEmail = Objects.requireNonNull(email);
        this.mImg = Objects.requireNonNull(img);
    }

    /**
     * Getter for  the image of the user.
     *
     * @return The image of the user.
     */
    public Bitmap getImg() {
        return mImg;
    }

    /**
     * Getter for the email of the user.
     *
     * @return The email of the user.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Setter for the email of the user.
     *
     * @param email The new email of the user.
     */
    public void setEmail(String email) {
        mEmail = email;
    }
}