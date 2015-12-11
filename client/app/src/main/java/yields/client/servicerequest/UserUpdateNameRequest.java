package yields.client.servicerequest;

import java.util.Objects;

import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to update a User.
 */
public class UserUpdateNameRequest extends ServiceRequest {

    private final User mUser;

    /**
     * Main constructor for this type of ServiceRequest (updating a User).
     *
     * @param user The ClientUser sending the request.
     */
    public UserUpdateNameRequest(User user) {
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
        return RequestKind.USER_UPDATE_NAME;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.userUpdateNameRequest(mUser.getId(), mUser.getName());
    }

    /**
     * Returns the User that should be updated through this request.
     *
     * @return The User that should be updated through this request.
     */
    public User getUser() {
        return mUser;
    }
}
