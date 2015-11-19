package yields.client.service;

import android.os.Binder;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import yields.client.activities.NotifiableActivity;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.servicerequest.GroupHistoryRequest;
import yields.client.servicerequest.NodeMessageRequest;

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
    // TODO: Note done yet but good base
    /*public void createNewGroup(Group group) {
        Objects.requireNonNull(group);
        List<Id> memberIDs = new ArrayList<>();
        List<User> members = group.getUsers();
        for (User u : members){
            memberIDs.add(u.getId());
        }
        ServerRequest groupAddServerRequest = RequestBuilder
                .groupCreateRequest(group.getUsers().get(0).getId(), group.getName(),
                        group.getVisibility(), memberIDs);
        Log.d("REQUEST", "Add new group");
        mService.sendRequest(groupAddServerRequest);
    }*/

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
    public void sendMessage(Group group, Message message) {
        Objects.requireNonNull(group);
        Objects.requireNonNull(message);

        mService.sendRequest(new NodeMessageRequest(message, group));
    }

    /**
     * Add older message to the Message Activity
     *
     * @param group The group we want to retrieve from
     * @param lastDate The last date we have in the history
     */
    public void addMoreGroupMessages(Group group,
                                     Date lastDate) {
        Objects.requireNonNull(group);
        Objects.requireNonNull(lastDate);

        mService.sendRequest(new GroupHistoryRequest(group, lastDate));
    }
}
