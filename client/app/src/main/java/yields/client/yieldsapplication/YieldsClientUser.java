package yields.client.yieldsapplication;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.ConnectionManager;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerChannel;
import yields.client.serverconnection.YieldEmulatorSocketProvider;

public class YieldsClientUser extends ClientUser{
    private static YieldsClientUser mInstance;
    private static YieldEmulatorSocketProvider mSocketProvider;
    private static ConnectionManager mConnectionManager;
    private static ServerChannel mServerChannel;

    private YieldsClientUser(String name, Id id, String email, Bitmap img)
            throws NodeException, IOException {
        super(name, id, email, img);
        mSocketProvider = new YieldEmulatorSocketProvider();
        mConnectionManager = new ConnectionManager(mSocketProvider);
        mServerChannel = (ServerChannel) mConnectionManager
                .getCommunicationChannel();
    }

    public static void createInstance(String name, Id id, String email, Bitmap img)
            throws InstantiationException, IOException {

        if (mInstance == null){
            mInstance = new YieldsClientUser(name, id, email, img);
            YieldsApplication.setUser(mInstance);
        }
        else{
            throw new InstantiationException(
                    "YieldsClientUser is already instanced.");
        }
    }

    @Override
    public void sendMessage(Group group, Message message) throws IOException {
        Request groupMessageReq = createRequestForMessageToSend(group, message);
        Response response = mServerChannel.sendRequest(groupMessageReq);
    }

    @Override
    public List<Message> getGroupMessages(Group group,
                                          Date lastDate)
            throws IOException {

        final int MESSAGE_COUNT = 20;

        Request groupHistoryRequest = RequestBuilder
                .GroupHistoryRequest(this.getId(), lastDate, MESSAGE_COUNT);
        mServerChannel.sendRequest(groupHistoryRequest);
        return null;
        // TODO : return the parsed messages.
    }

    @Override
    public void addNewGroup(Group group) throws IOException {
        List<Id> memberIDs = new ArrayList<>();
        List<User> members = group.getUsers();
        for (User u : members){
            memberIDs.add(u.getId());
        }
        Request groupAddRequest = RequestBuilder
                .GroupCreateRequest(this.getId(), group.getName(), memberIDs);
        Response response = mServerChannel.sendRequest(groupAddRequest);
    }

    @Override
    public void deleteGroup(Group group) {

    }

    @Override
    public Map<User, String> getHistory(Group group, Date from) {
        return null;
    }

    /**
     * Create a request for the corresponding message to send to the server.
     * @param group Group to send the message.
     * @param message   The message itself.
     * @return  The correct request, ready to be sent.
     */
    public static Request createRequestForMessageToSend(Group group, Message message){
        Objects.requireNonNull(group);
        Objects.requireNonNull(message);
        Request req;
        switch (message.getContent().getType()) {
            case "image":
                req = RequestBuilder
                        .GroupImageMessageRequest(message.getSender().getId(), group.getId(),
                                "text", (ImageContent) message.getContent());
                break;
            case "text":
                req = RequestBuilder
                        .GroupMessageRequest(message.getSender().getId(), group.getId(),
                                "text", ((TextContent) message
                                        .getContent()).getText());
                break;
            default : throw new IllegalArgumentException("type unknown");
        }
        return req;
    }
}
