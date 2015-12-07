package yields.client.node;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import yields.client.R;
import yields.client.exceptions.NodeException;
import yields.client.gui.GraphicTransforms;
import yields.client.id.Id;
import yields.client.serverconnection.ImageSerialization;
import yields.client.serverconnection.Response;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Connected user who will do the connexion with the outside world.
 */
public class ClientUser extends User {

    private List<Group> mGroups;
    private final List<User> mEntourage;

    /**
     * Creates a client user which represents the client connected to the application.
     *
     * @param name  The name of the User.
     * @param id    The id of the User.
     * @param email The Email of the User.
     * @param img   The image of the User.
     */
    public ClientUser(String name, Id id, String email, Bitmap img) {
        super(name, id, email, img);
        mGroups = new ArrayList<>();
        mEntourage = new ArrayList<>();
    }

    /**
     * constructs a shell for a clientUser waiting for update from server.
     *
     * @param email The email of the user.
     */
    public ClientUser(String email) {
        this("", new Id(0l), email, YieldsApplication.getDefaultUserImage());
    }

    /**
     * Add groups to the user
     *
     * @param groups the groups to add
     */
    public void addGroups(List<Group> groups) {
        mGroups.clear();
        mGroups.addAll(groups);
    }

    /**
     * Add a group to the user
     *
     * @param group the group to add.
     */
    public void addGroup(Group group) {
        for (Group prevGroup : mGroups) {
            if (prevGroup.getId().getId().equals(group.getId().getId())) {
                return;
            }
        }
        mGroups.add(group);
    }

    /**
     * Get an unmodifiable sorted list of the user groups.
     *
     * @return The sus-mentioned list.
     */
    public List<Group> getUserGroups() {
        Collections.sort(mGroups, mComparator);
        return Collections.unmodifiableList(mGroups);
    }

    /**
     * Adds a User to the entourage.
     *
     * @param user the user to add.
     */
    public void addUserToEntourage(User user) {
        mEntourage.add(user);
    }

    /**
     * Gets an unmodifiable list of users.
     *
     * @return The list of user connected to this user.
     */
    public List<User> getEntourage() {
        return Collections.unmodifiableList(mEntourage);
    }

    /**
     * The group comparator.
     */
    private final Comparator<Group> mComparator = new Comparator<Group>() {

        @Override
        public int compare(Group lhs, Group rhs) {
            return rhs.getLastUpdate().compareTo(lhs.getLastUpdate());
        }
    };

    /**
     * Activates the group when we received confirmation from the server.
     *
     * @param id The id of the group to activate.
     */
    //TODO: to be changed when response from server changed
    public void activateGroup(Id id) {
        for (Group group : mGroups) {
            if (!group.isValidated()) {
                group.setValidated();
                group.setId(id);
            }
        }
    }

    /**
     * Get group to be modified.
     *
     * @param groupId The group Id.
     * @return The group to be modified
     */
    public Group modifyGroup(Id groupId) {
        for (Group group :
                mGroups) {
            if (group.getId().equals(groupId)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Getter for a User from the entourage.
     * Returns null if the Id of the User is not in the entourage.
     *
     * @param userId The id of the User.
     * @return The User from the entourage with the same id or null
     * if there is no User with that Id in the entourage.
     */
    public User getUserFromEntourage(Id userId) {
        for (User user : mEntourage) {
            if (userId.equals(user.getId())) {
                return user;
            }
        }

        return null;
    }
}
