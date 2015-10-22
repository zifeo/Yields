package yields.client.node;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.Message;

/**
 * Connected user who will do the connexion with the outside world
 *
 */
public abstract class ClientUser extends User {
    List<Group> groups;

    public ClientUser(String name, Id id, String email, Bitmap img) throws NodeException {
        super(name, id, email,img);
        this.groups = new ArrayList<>();
    }

    public abstract void sendMessage(Group group, Message message);

    public abstract List<Message> getGroupMessages(Group group);

    public abstract void addNewGroup(Group group);

    public abstract void deleteGroup(Group group);

    public abstract Map<User, String> getHistory(Group group, Date from);

    public List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }
}
