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
import yields.client.serverconnection.Response;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Connected user who will do the connexion with the outside world.
 */
public class ClientUser extends User {

    private List<Group> mGroups;
    private final List<User> mEntourage;

    public ClientUser(String name, Id id, String email, Bitmap img) throws NodeException {
        super(name, id, email, img);
        this.mGroups = new ArrayList<>();
        mEntourage = new ArrayList<>();
    }

    /**
     * constructs a shell for a clientUser waiting for update from server.
     * @param email The email of the user.
     * @throws NodeException
     */
    public ClientUser(String email) throws NodeException {
        this("", new Id(0l), email, YieldsApplication.getDefaultUserImage());
    }

    public void addGroups(List<Group> groups) {
        mGroups.clear();
        mGroups.addAll(groups);
        Collections.sort(mGroups, mComparator);
    }

    public void addGroup(Group group) {
        mGroups.add(group);
        Collections.sort(mGroups, mComparator);
    }

    public List<Group> getUserGroups() {
        return Collections.unmodifiableList(mGroups);
    }

    public void update(JSONObject response) throws JSONException{
        // TODO : change profil pic
        this.setName(response.getString("name"));
        this.setEmail(response.getString("email"));
    }

    public void addUserToEntourage(User user) {
        mEntourage.add(user);
    }

    public List<User> getEntourage() {
        return Collections.unmodifiableList(mEntourage);
    }

    private final Comparator<Group> mComparator = new Comparator<Group>() {

        @Override
        public int compare(Group lhs, Group rhs) {
            return lhs.getLastUpdate().compareTo(rhs.getLastUpdate());
        }
    };

    //TODO: to be changed when response from server changed
    public void activateGroup(Id id) {
        for (Group group : mGroups) {
            if (!group.isValidated()) {
                group.setValidated();
                group.setId(id);
            }
        }
    }

    public Group modifyGroup(Id groupId) {
        for (Group group :
                mGroups) {
            if (group.getId().equals(groupId)) {
                return group;
            }
        }
        return null;
    }

    public User modifyEntourage(Id userId) {
        for (User user : mEntourage) {
            if (userId.equals(user.getId())) {
                return user;
            }
        }

        return null;
    }
}
