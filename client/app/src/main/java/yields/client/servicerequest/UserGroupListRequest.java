package yields.client.servicerequest;

import java.util.Objects;

import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to retrieve all Groups for a User.
 */
public class UserGroupListRequest extends ServiceRequest {

    private final User mUser;

    /**
     * Main constructor for this type of ServiceRequest (retrieve all Groups of a User).
     *
     * @param user The ClientUser sending the request.
     */
    public UserGroupListRequest(User user) {
        super();
        Objects.requireNonNull(user);

        mUser = user;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.USER_NODE_LIST;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.userGroupListRequest(mUser.getId());
    }
}
