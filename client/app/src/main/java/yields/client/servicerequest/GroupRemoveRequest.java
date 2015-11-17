package yields.client.servicerequest;

import android.app.Service;

import java.util.Objects;

import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to remove a User from a Group.
 */
public class GroupRemoveRequest extends ServiceRequest {

    private final User mSender;
    private final Group mGroup;
    private final User mUser;

    /**
     * Main constructor for this type of ServiceRequest (removing a User from a Group).
     *
     * @param sender  The User that created this request.
     * @param group   The Group that should a User removed from it.
     * @param userToRemove The User that should be removed from the Group.
     */
    public GroupRemoveRequest(User sender, Group group, User userToRemove) {
        super();
        Objects.requireNonNull(sender);
        Objects.requireNonNull(group);
        Objects.requireNonNull(userToRemove);

        mSender = sender;
        mGroup = group;
        mUser = userToRemove;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUP_REMOVE;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.groupRemoveRequest(mSender.getId(), mGroup.getId(), mUser.getId());
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {
        //TODO : @Trofleb
    }
}
