package yields.client.node;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Nicolas on 16.10.15.
 */
public class ClientUser extends User {
    List<Group> groups;

    public ClientUser(String name, long id, String email) {
        super(name, id, email);
        //TODO : get groups from connexion
        this.groups = new ArrayList<>();
    }

    public void sendMessage(Group group) {
        //TODO : to implement
    }

    public void addNewGroup(Group group) {
        //TODO : to implement
    }

    public void deleteGroup(Group group) {
        //TODO : to implement
    }

    public Map<User, String> getHistory(Date from) {
        //TODO : to implement
        return new ArrayMap<>();
    }

    public List<Group> getGroups() {
        return groups;
    }
}
