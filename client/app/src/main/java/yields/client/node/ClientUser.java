package yields.client.node;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import yields.client.messages.Message;

/**
 * Connected user who will do the connexion with the outside world
 *
 */
public abstract class ClientUser extends User {
    List<Group> groups;

    public ClientUser(String name, long id, String email) {
        super(name, id, email);
        this.groups = new ArrayList<>();
    }

    public abstract void sendMessage(Group group, Message message);

    public abstract void addNewGroup(Group group);

    public abstract void deleteGroup(Group group);

    public abstract Map<User, String> getHistory(Date from);

    public List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }
}
