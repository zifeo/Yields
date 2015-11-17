package yields.client.servicerequest;

import android.app.Service;

import java.util.Objects;

import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to update a User.
 */
public class UserUpdateRequest extends ServiceRequest {

    private final User mUser;

    /**
     * Main constructor for this type of ServiceRequest (updating a User).
     *
     * @param user The ClientUser sending the request.
     */
    public UserUpdateRequest(User user) {
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
        return RequestKind.USER_UPDATE;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.userUpdateRequest(mUser.getId());
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {

    }
}
