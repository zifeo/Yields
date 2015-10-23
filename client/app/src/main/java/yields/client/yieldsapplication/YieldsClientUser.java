package yields.client.yieldsapplication;

import android.graphics.Bitmap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.serverconnection.ConnectionManager;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerChannel;
import yields.client.serverconnection.YieldEmulatorSocketProvider;

public class YieldsClientUser extends ClientUser{
    private static YieldsClientUser mInstance;
    private static YieldEmulatorSocketProvider mSocketProvider;
    private static ConnectionManager mConnectionManager;
    private static ServerChannel mServerChannel;

    private YieldsClientUser(String name, Id id, String email, Bitmap img) throws NodeException, IOException {
        super(name, id, email, img);
        mSocketProvider = new YieldEmulatorSocketProvider();
        mConnectionManager = new ConnectionManager(mSocketProvider);
        mServerChannel = (ServerChannel) mConnectionManager.getCommunicationChannel();
    }

    public ClientUser getInstance() {
        Objects.requireNonNull(mInstance, "Singleton YieldsClientUser has not been instanced yet.");
        return mInstance;
    }

    public void createInstance(String name, Id id, String email, Bitmap img) throws InstantiationException, IOException {
        if (mInstance == null){
            mInstance = new YieldsClientUser(name, id, email, img);
        }
        else{
            throw new InstantiationException("YieldsClientUser is already instanced.");
        }
    }

    @Override
    public void sendMessage(Group group, Message message) throws IOException {
        if (message.getContent().getType() == "image"){
            throw new UnsupportedOperationException("Error, sending image messages is not yet supported.");
        }
        TextContent content = (TextContent) message.getContent();
        Request groupMessageReq = RequestBuilder.GroupMessageRequest(this.getId(), group.getId(), "text", content.getText());
        mServerChannel.sendRequest(groupMessageReq);
    }

    @Override
    public List<Message> getGroupMessages(Group group) throws IOException {
        final int MESSAGE_COUNT = 100;
        Request groupHistoryRequest = RequestBuilder.GroupHistoryRequest(this.getId(), MESSAGE_COUNT);
        mServerChannel.sendRequest(groupHistoryRequest);
        return null;
        // TODO : return the parsed messages.
    }

    @Override
    public void addNewGroup(Group group) throws IOException {
        HashMap<RequestBuilder.Fields, String> memberEmails = new HashMap<>();
        List<User> members = group.getUsers();
        for (User u : members){
            memberEmails.put(RequestBuilder.Fields.EMAIL, u.getEmail());
        }
        Request groupAddRequest = RequestBuilder.GroupAddRequest(this.getId(), memberEmails);
        mServerChannel.sendRequest(groupAddRequest);
    }

    @Override
    public void deleteGroup(Group group) {

    }

    @Override
    public Map<User, String> getHistory(Group group, Date from) {
        return null;
    }
}
