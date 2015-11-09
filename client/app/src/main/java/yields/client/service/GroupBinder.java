package yields.client.service;

import android.os.Binder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yields.client.activities.GroupActivity;
import yields.client.activities.MessageActivity;
import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.RequestBuilder;

public class GroupBinder extends Binder {
    private final YieldService mService;

    /**
     * Creates the binder and links it to the service
     * @param service The Service concerned
     */
    public GroupBinder(YieldService service) {
        mService = service;
    }

    /**
     * Binds the activity to the service which permits it to receive incoming messages directly
     * @param activity the current messagingActivity
     */
    public void attachActivity(GroupActivity activity) {
        Objects.requireNonNull(activity);
        mService.setNotifiableActivity(activity);
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
}
