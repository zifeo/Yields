package yields.client.node;

import java.io.IOException;
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
import yields.client.yieldsapplication.YieldsApplication;

public class Group extends Node {
    private List<User> mConnectedUsers;
    private TreeMap<Date, Message> mMessages;
    private boolean mConsumed;

    public Group(String name, Id id, List<User> connectedUsers){
        super(name, id);
        Objects.requireNonNull(connectedUsers);
        this.mConnectedUsers = new ArrayList<>(connectedUsers);
        this.mMessages = new TreeMap<>();
        mConsumed = false;
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

    public SortedMap<Date, Message> getLastMessages() throws IOException{
        Map.Entry<Date, Message> message = mMessages.firstEntry();

        Date farthestDate;

        if (message == null) {
            farthestDate = new Date();
        } else {
            farthestDate = message.getKey();
        }

        List<Message> messages = YieldsApplication.getUser()
                .getGroupMessages(this, farthestDate);

        for(Message m : messages){
            mMessages.put(m.getDate(),m);
        }

        return Collections.unmodifiableSortedMap(mMessages
                .headMap(farthestDate));
    }

    public Message getLastMessage(){
        return mMessages.firstEntry().getValue();
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(mConnectedUsers);
    }
}