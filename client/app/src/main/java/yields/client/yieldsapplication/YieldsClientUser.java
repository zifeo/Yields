package yields.client.yieldsapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import yields.client.exceptions.NodeException;
import yields.client.id.Id;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.serverconnection.ConnectionManager;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerChannel;
import yields.client.serverconnection.YieldEmulatorSocketProvider;

/**
 * The User who does the communication with the server
 * Represents also the client user itself
 */
public class YieldsClientUser extends ClientUser{
    private static YieldsClientUser mInstance;
    private static YieldEmulatorSocketProvider mSocketProvider;
    private static ConnectionManager mConnectionManager;
    private static ServerChannel mServerChannel;

    private YieldsClientUser(String name, Id id, String email, Bitmap img)
            throws NodeException, InstantiationException, IOException {
        super(name, id, email, img);
        mSocketProvider = new YieldEmulatorSocketProvider();
        mConnectionManager = new ConnectionManager(mSocketProvider);
        mServerChannel = (ServerChannel) mConnectionManager
                .getCommunicationChannel();
    }

    /**
     * Creates an instance of YieldClientUser (which is unique)
     *
     * @param name The name of the current User
     * @param id The id of the current User
     * @param email The e-mail of the current User
     * @param img The profile picture of the current User
     * @throws InstantiationException In case there already exists another YieldClientUser
     * @throws IOException If we have trouble creating the server connection
     */
    public static void createInstance(String name, Id id, String email, Bitmap img)
            throws InstantiationException, IOException, ExecutionException, InterruptedException {

        mInstance  = new CreateInstanceTask().execute(name, id, email, img).get();
        YieldsApplication.setUser(mInstance);
    }

    /**
     * Sends a message to the server
     * @param group The group to which is linked the message
     * @param message The message itself
     * @throws IOException in case of communication errors
     */
    @Override
    public void sendMessage(Group group, Message message) throws IOException {
        Request groupMessageReq = createRequestForMessageToSend(group, message);
        Log.d("REQUEST", "sendMessage = " + groupMessageReq.message());
        Response response = mServerChannel.sendRequest(groupMessageReq);
        // TODO : Check response.
    }

    /**
     * Get older group messages.
     * @param group The group you want to get messages from
     * @param lastDate The lastDate of the last message retrieved
     * @return a list of the older messages
     * @throws IOException In case of communication error
     */
    @Override
    public List<Message> getGroupMessages(Group group,
                                          Date lastDate)
            throws IOException {
        final int MESSAGE_COUNT = 20;

        Request groupHistoryRequest = RequestBuilder
                .GroupHistoryRequest(group.getId(), lastDate, MESSAGE_COUNT);
        Response response = mServerChannel.sendRequest(groupHistoryRequest);
        Log.d("REQUEST", "getGroupMessages " + groupHistoryRequest.message());
        List<Message> messageList = new ArrayList<>();

        try {
            JSONArray responseArray = response.object()
                    .getJSONObject("message").getJSONArray("nodes");

            for (int i = 0; i < responseArray.length(); i++) {
                messageList.add(new Message(responseArray.getJSONObject(i)));
                Log.d("REQUEST", "    Message " + i + " : " + ((TextContent) messageList.get(i).getContent()).getText());
            }

        } catch (JSONException e) {
            throw new IOException();
        }

        return messageList;
    }

    /**
     * Creates a new group on the server.
     * @param group The group to be created
     * @throws IOException in case of connexion error
     */
    @Override
    public void createNewGroup(Group group) throws IOException {
        List<Id> memberIDs = new ArrayList<>();
        List<User> members = group.getUsers();
        for (User u : members){
            memberIDs.add(u.getId());
        }
        Request groupAddRequest = RequestBuilder
                .GroupCreateRequest(this.getId(), group.getName(), memberIDs);
        Log.d("REQUEST", "Add new group");
        Response response = mServerChannel.sendRequest(groupAddRequest);
        // TODO : Check response
    }

    @Override
    public void deleteGroup(Group group) {
        // TODO : See with the server side how to implement it.
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

    private static class CreateInstanceTask extends AsyncTask<Object, Void, YieldsClientUser>{
        @Override
        protected YieldsClientUser doInBackground(Object... params) {
            try {
                return  new YieldsClientUser((String) params[0], (Id) params[1], (String)params[2], (Bitmap) params[3]);
            } catch (InstantiationException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
