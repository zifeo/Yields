package yields.client.servicerequest;

import android.app.Service;

import java.util.Objects;

import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to remove a User of another User's entourage.
 */
public class UserEntourageRemoveRequest extends ServiceRequest {

    private final User mUser;
    private final User mUserToRemove;

    /**
     * Main constructor for this type of ServiceRequest (removing a User from another User's
     * entourage).
     *
     * @param user            The User sending the request, and who wants his entourage modified..
     * @param userToBeRemoved The User that will be removed from user's entourage.
     */
    public UserEntourageRemoveRequest(User user, User userToBeRemoved) {
        super();
        Objects.requireNonNull(user);
        Objects.requireNonNull(userToBeRemoved);

        mUser = user;
        mUserToRemove = userToBeRemoved;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.USER_ENTOURAGE_REMOVE;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.userEntourageRemoveRequest(mUser.getId(), mUserToRemove.getEmail());
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {

    }
}
