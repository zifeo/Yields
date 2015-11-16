package yields.client.servicerequest;

import android.app.Service;

import java.util.ArrayList;
import java.util.List;

import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to create a Group.
 */
public class GroupCreateRequest extends ServiceRequest {

    private final User mCreator;
    private final Group mGroup;

    /**
     * Main constructor for this type of ServiceRequest (creating a Group).
     *
     * @param creator The User that created the Group.
     * @param group   The Group that should be created.
     */
    public GroupCreateRequest(User creator, Group group) {
        super();
        mCreator = creator;
        mGroup = group;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUPCREATE;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        List<Id> userIds = new ArrayList<>();
        List<User> users = mGroup.getUsers();
        for (User user : users) {
            userIds.add(user.getId());
        }
        return RequestBuilder.groupCreateRequest(mCreator.getId(), mGroup.getName(), mGroup
                .getVisibility(), userIds);
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {
        //TODO : @Trofleb
    }
}
