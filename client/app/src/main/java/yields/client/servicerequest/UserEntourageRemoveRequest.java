package yields.client.servicerequest;

import java.util.Objects;

import yields.client.id.Id;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to remove a User of another User's entourage.
 */
public class UserEntourageRemoveRequest extends ServiceRequest {

    private final Id mUser;
    private final Id mUserToRemove;

    /**
     * Main constructor for this type of ServiceRequest (removing a User from another User's
     * entourage).
     *
     * @param senderId        The Id of the User sending the request, and who wants his entourage modified..
     * @param userToBeRemoved The Id of the User that will be removed from user's entourage.
     */
    public UserEntourageRemoveRequest(Id senderId, Id userToBeRemoved) {
        super();
        Objects.requireNonNull(senderId);
        Objects.requireNonNull(userToBeRemoved);

        mUser = senderId;
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
        return RequestBuilder.userEntourageRemoveRequest(mUser, mUserToRemove);
    }

    /**
     * Returns the Id of the User that should be removed from the entourage.
     *
     * @return The Id of the User that should be removed from the entourage.
     */
    public Id getUserToRemove() {
        return mUserToRemove;
    }
}
