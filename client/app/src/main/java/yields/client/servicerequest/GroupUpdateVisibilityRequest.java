package yields.client.servicerequest;

import android.app.Service;

import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to update a Group's visibility.
 */
public class GroupUpdateVisibilityRequest extends ServiceRequest {

    private final User mSender;
    private final Group mGroup;
    private final Group.GroupVisibility mVisibility;

    /**
     * Main constructor for this type of ServiceRequest (updating a Group's visibility).
     *
     * @param sender     The User that created this request.
     * @param group      The Group that should have it's visibility changed.
     * @param visibility The new visibility of the Group
     */
    public GroupUpdateVisibilityRequest(User sender, Group group, Group.GroupVisibility visibility) {
        super();
        mSender = sender;
        mGroup = group;
        mVisibility = visibility;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUPUPDATEVISIBILITY;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.groupUpdateVisibilityRequest(mSender.getId(), mGroup.getId(),
                mVisibility);
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {
        //TODO : @Trofleb
    }
}
