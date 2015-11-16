package yields.client.servicerequest;

import android.app.Service;

import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to rename a Group.
 */
public class GroupUpdateNameRequest extends ServiceRequest {

    private final User mSender;
    private final Group mGroup;
    private final String mName;

    /**
     * Main constructor for this type of ServiceRequest (creating a Group).
     *
     * @param sender The User that created this request.
     * @param group  The Group that should be renamed.
     * @param name   The new name of the Group.
     */
    public GroupUpdateNameRequest(User sender, Group group, String name) {
        super();
        mSender = sender;
        mGroup = group;
        mName = name;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUPUPDATENAME;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.groupUpdateNameRequest(mSender.getId(), mGroup.getId(), mName);
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {
        //TODO : @Trofleb
    }
}
