package yields.client.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.Message;

public class Group extends Node {
    private List<User> mConnectedUsers;
    private TreeMap<Date, Message> mMessages;

    public Group(String name, Id id, List<User> connectedUsers){
        super(name, id);
        Objects.requireNonNull(connectedUsers);
        this.mConnectedUsers = new ArrayList<>(connectedUsers);
    }

    private void connectUser(User user) throws NodeException {
        User newUser = new User(user.getName(), user.getId(),
                user.getEmail(), user.getImg());
        mConnectedUsers.add(newUser);
    }

    public void appendUsers(Collection<User> users) throws NodeException {
        for (User user: users ) {
            connectUser(user);
        }
    }

    public SortedMap<Date, Message> getMessages(){
        return Collections.unmodifiableSortedMap(mMessages);
    }

    public Message getLastMessage(){
        return mMessages.firstEntry().getValue();
    }

    public void appendMessages(Map<Date, Message> messageMap){
        mMessages.putAll(messageMap);
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(mConnectedUsers);
    }
}