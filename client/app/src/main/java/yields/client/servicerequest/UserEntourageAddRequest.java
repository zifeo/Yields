package yields.client.servicerequest;

import java.util.Objects;

import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to add a User to another User's entourage.
 */
public class UserEntourageAddRequest extends ServiceRequest {

    private final User mUser;
    private final User mUserToAdd;

    /**
     * Main constructor for this type of ServiceRequest (adding a User to another User's entourage).
     *
     * @param user          The User that send this request and which wants it's entourage modified.
     * @param userToBeAdded The User that will be added to user's entourage.
     */
    public UserEntourageAddRequest(User user, User userToBeAdded) {
        super();
        Objects.requireNonNull(user);
        Objects.requireNonNull(userToBeAdded);

        mUser = user;
        mUserToAdd = userToBeAdded;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.USER_ENTOURAGE_ADD;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.userEntourageAddRequest(mUser.getId(), mUserToAdd.getEmail());
    }

    /**
     * Returns the User that should be added to the entourage.
     *
     * @return The User that should be added to the entourage.
     */
    public User getUserToAdd() {
        return mUserToAdd;
    }
}
