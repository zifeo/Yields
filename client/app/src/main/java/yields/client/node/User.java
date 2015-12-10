package yields.client.node;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.serverconnection.ImageSerialization;
import yields.client.yieldsapplication.YieldsApplication;

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
                String email, Bitmap img) {
        super(name, id);
        this.mEmail = Objects.requireNonNull(email);
        this.mImg = Objects.requireNonNull(img);
    }

    /**
     * Constructs a User from a server Response message.
     *
     * @param response The response from the server.
     * @throws JSONException
     */
    public User(JSONObject response) throws JSONException{
        this(response.getString("name"), new Id(response.getLong("uid")),
                response.getString("email"), YieldsApplication.getDefaultUserImage());

        if (!response.optString("pic").equals("")) {
            this.setImg(ImageSerialization
                    .unSerializeImage(response.getString("pic")));
        } else {
            this.setImg(YieldsApplication.getDefaultUserImage());
        }
    }

    /**
     * Updates the User from a Json response
     *
     * @param response the JSONObject from the response
     * @throws JSONException In case of trouble parsing the response.
     */
    public void update(JSONObject response) throws JSONException{
        this.setName(response.getString("name"));
        this.setEmail(response.getString("email"));

        if (!response.optString("pic").equals("")) {
            this.setImg(ImageSerialization
                    .unSerializeImage(response.getString("pic")));
        } else {
            this.setImg(YieldsApplication.getDefaultUserImage());
        }
    }

    /**
     * Updates the User from an other instance of User.
     *
     * @param userUpdated the JSONObject from the response
     */
    public void update(User userUpdated) {
        this.setName(userUpdated.getName());
        this.setEmail(userUpdated.getEmail());
        this.setImg(userUpdated.getImg());
    }

    /**
     * constructs a shell for a User waiting for update from server.
     * @param id The id of the user.
     */
    public User(Id id) {
        this("", id, "", YieldsApplication.getDefaultUserImage());
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
     * Setter for the user picture
     * @param img The new image
     */
    public void setImg(Bitmap img) {
        mImg = Objects.requireNonNull(img);
    }

    /**
     * Setter for the email of the user.
     *
     * @param email The new email of the user.
     */
    public void setEmail(String email) {
        mEmail = email;
    }

    /**
     * Override of the equals method.
     * Performs an equality test on this User.
     * Two users are equal if they have the same Id.
     *
     * @param other The Object to be compared with.
     * @return True if the equality holds, false otherwise.
     */
    @Override
    public boolean equals(Object other){
        if (!(other instanceof User)) {
            return false;
        } else {
            return ((User) other).getId().equals(this.getId());
        }
    }
}