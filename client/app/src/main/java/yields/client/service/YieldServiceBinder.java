package yields.client.service;

import android.os.Binder;
import android.util.Log;

import java.util.Objects;

import yields.client.activities.NotifiableActivity;
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
    public void connectionStatus(){
        mService.connectionStatusRequest();
    }

    /**
     * Send a request to server via the Service.
     * @param request The request to be sent.
     */
    public void sendRequest(ServiceRequest request) {
        Objects.requireNonNull(request);
        mService.sendRequest(request);
    }
}
