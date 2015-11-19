package yields.client.service;

import android.os.Binder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import yields.client.activities.NotifiableActivity;
import yields.client.fragments.GroupMessageFragment;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.ServerRequest;
import yields.client.serverconnection.RequestBuilder;
import yields.client.servicerequest.GroupHistoryRequest;
import yields.client.servicerequest.GroupMessageRequest;
import yields.client.servicerequest.ServiceRequest;

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
     * Send a request to server via the Service.
     * @param request
     */
    public void sendRequest(ServiceRequest request){
        Objects.requireNonNull(request);
        mService.sendRequest(request);
    }
}
