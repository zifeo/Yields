package yields.client.servicerequest;

import android.app.Service;

import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to retrieve all Groups for a User.
 */
public class UserEntourageAddRequest extends ServiceRequest {

    private final User mUser;
    private final User mUserToAdd;

    /**
     * Main constructor for this type of ServiceRequest (adding a User to another User's entourage).
     *
     * @param user The ClientUser sending the request.
     */
    public UserEntourageAddRequest(User user, User userToBeAdded){
        super();
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
        return RequestKind.USERENTOURAGEADD;
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

    @Override
    public void serviceActionOnResponse(Service service, Response response) {

    }
}
