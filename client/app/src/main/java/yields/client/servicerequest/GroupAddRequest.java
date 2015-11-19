package yields.client.servicerequest;

import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to add a User to a Group.
 */
public class GroupAddRequest extends ServiceRequest {

    private final User mSender;
    private final Id mGroupId;
    private final User mUser;

    /**
     * Main constructor for this type of ServiceRequest (adding a User to a Group).
     *
     * @param sender  The User that created this request.
     * @param groupId The Group that should have a User added to it.
     * @param newUser The User that should be added to the Group.
     */
    public GroupAddRequest(User sender, Id groupId, User newUser) {
        super();
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newUser);

        mSender = sender;
        mGroupId = groupId;
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
        return RequestBuilder.groupAddRequest(mSender.getId(), mGroupId, mUser.getId());
    }

    /**
     * Returns the Id of the Group to which the User will be added.
     *
     * @return The Id of the Group to which the User will be added.
     */
    public Id getGroupId() {
        return mGroupId;
    }

    /**
     * Returns the User that will be added to the Group.
     *
     * @return The User that will be added to the Group.
     */
    public User getUser() {
        return mUser;
    }
}
