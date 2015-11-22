package yields.client.node;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.serverconnection.Response;

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

    public void addGroups(List<Group> groups) {
        mGroups = groups;
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
}
