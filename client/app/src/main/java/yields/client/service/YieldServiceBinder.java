package yields.client.service;

import android.os.Binder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import yields.client.activities.GroupActivity;
import yields.client.activities.MessageActivity;
import yields.client.activities.NotifiableActivity;
import yields.client.id.Id;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.RequestBuilder;

public class YieldServiceBinder extends Binder {
    private final YieldService mService;

    /**
     * Creates the binder and links it to the service
     * @param service The Service concerned
     */
    public YieldServiceBinder(YieldService service) {
        mService = service;
    }

    /**
     * Binds the activity to the service which permits it to receive incoming messages directly
     * @param activity the current messagingActivity
     */
    public void attachActivity(NotifiableActivity activity) {
        Objects.requireNonNull(activity);
        mService.setNotifiableActivity(activity);
    }

    public void unsetMessageActivity(){
        mService.unsetMessageActivity();
    }

    /**
     * Creates a group on the server
     *
     * @param group the group to create
     */
    public void createNewGroup(Group group) {
        List<Id> memberIDs = new ArrayList<>();
        List<User> members = group.getUsers();
        for (User u : members){
            memberIDs.add(u.getId());
        }
        Request groupAddRequest = RequestBuilder
                .GroupCreateRequest(group.getUsers().get(0).getId(), group.getName(), memberIDs);
        Log.d("REQUEST", "Add new group");
        mService.sendRequest(groupAddRequest);
    }

    /**
     * Can be used to know if the server is connected to the server
     */
    public boolean isServerConnected(){
        //TODO implement a mean of knowing if the server is connected
        return true;
    }

    /**
     * Sends a message to the server
     * @param group The group to which is linked the message
     * @param message The message itself
     * @throws IOException in case of communication errors
     */
    public void sendMessage(Group group, Message message){
        Request groupMessageReq = createRequestForMessageToSend(group, message);
        Log.d("REQUEST", "sendMessage = " + groupMessageReq.message());
        mService.sendRequest(groupMessageReq);
    }

    /**
     * Add older message to the Message Activity
     *
     * @param group The group we want to retrieve from
     * @param lastDate The last date we have in the history
     * @param messageCount The max number of message we want
     */
    public void addMoreGroupMessages(Group group,
                                     Date lastDate, int messageCount) {
        Request groupHistoryRequest = RequestBuilder
                .GroupHistoryRequest(group.getId(), lastDate, messageCount);
        mService.sendRequest(groupHistoryRequest);
        Log.d("REQUEST", "getGroupMessages " + groupHistoryRequest.message());
    }

    /**
     * Build a request for sending a message to a group.
     * @param group The group receiving the message.
     * @param message The message to be sent to the group.
     * @return The request.
     */
    private static Request createRequestForMessageToSend(Group group, Message message){
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
                        .GroupTextMessageRequest(message.getSender().getId(), group.getId(),
                                "text", ((TextContent) message
                                        .getContent()).getText());
                break;
            default : throw new IllegalArgumentException("type unknown");
        }
        return req;
    }
}