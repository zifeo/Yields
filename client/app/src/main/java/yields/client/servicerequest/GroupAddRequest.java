package yields.client.servicerequest;

import android.app.Service;

import java.util.Objects;

import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to add a User to a Group.
 */
public class GroupAddRequest extends ServiceRequest {

    private final User mSender;
    private final Group mGroup;
    private final User mUser;

    /**
     * Main constructor for this type of ServiceRequest (adding a User to a Group).
     *
     * @param sender  The User that created this request.
     * @param group   The Group that should have a User added to it.
     * @param newUser The User that should be added to the Group.
     */
    public GroupAddRequest(User sender, Group group, User newUser) {
        super();
        Objects.requireNonNull(sender);
        Objects.requireNonNull(group);
        Objects.requireNonNull(newUser);

        mSender = sender;
        mGroup = group;
        mUser = newUser;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUP_ADD;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.groupAddRequest(mSender.getId(), mGroup.getId(), mUser.getId());
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {
        //TODO : @Trofleb
    }
}
