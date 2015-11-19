package yields.client.servicerequest;

import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service for information about a User.
 */
public class UserInfoRequest extends ServiceRequest {

    private final User mSender;
    private final Id mUserInfoId;

    /**
     * Main constructor for this type of ServiceRequest (retrieving User information).
     *
     * @param sender     The User which is sending the request.
     * @param userInfoId The Id of the User from which the information shall be retrieved..
     */
    public UserInfoRequest(User sender, Id userInfoId) {
        super();
        Objects.requireNonNull(sender);
        Objects.requireNonNull(userInfoId);

        mSender = sender;
        mUserInfoId = userInfoId;

    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.USER_INFO;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.userInfoRequest(mSender.getId(), mUserInfoId);
    }

    /**
     * Returns the Id of the User for which the sender wants information.
     *
     * @return The Id of the User for which the sender wants information.
     */
    public Id getUserInfoId() {
        return mUserInfoId;
    }
}
