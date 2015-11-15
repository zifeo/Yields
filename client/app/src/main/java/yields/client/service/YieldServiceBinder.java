package yields.client.service;

import android.os.Binder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import yields.client.activities.NotifiableActivity;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.ServerRequest;
import yields.client.serverconnection.RequestBuilder;

public class YieldServiceBinder extends Binder {
    private final YieldService mService;

    /**
     * Creates the binder and links it to the service
     * @param service The Service concerned
     */
    public YieldServiceBinder(YieldService service) {
        Objects.requireNonNull(service);
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
        Objects.requireNonNull(group);
        List<Id> memberIDs = new ArrayList<>();
        List<User> members = group.getUsers();
        for (User u : members){
            memberIDs.add(u.getId());
        }
        ServerRequest groupAddServerRequest = RequestBuilder
                .GroupCreateRequest(group.getUsers().get(0).getId(), group.getName(), memberIDs);
        Log.d("REQUEST", "Add new group");
        mService.sendRequest(groupAddServerRequest);
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
        Objects.requireNonNull(group);
        Objects.requireNonNull(message);
        //mService.sendRequest();
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
        Objects.requireNonNull(group);
        Objects.requireNonNull(lastDate);
        ServerRequest groupHistoryServerRequest = RequestBuilder
                .GroupHistoryRequest(group.getId(), lastDate, messageCount);
        mService.sendRequest(groupHistoryServerRequest);
        Log.d("REQUEST", "getGroupMessages " + groupHistoryServerRequest.message());
    }
}
