package yields.client.servicerequest;

import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to remove a User from a Group.
 */
public class GroupRemoveRequest extends ServiceRequest {

    private final User mSender;
    private final Id mGroupId;
    private final Id mUserId;

    /**
     * Main constructor for this type of ServiceRequest (removing a User from a Group).
     *
     * @param sender         The User that created this request.
     * @param groupId        The Group that should a User removed from it.
     * @param userToRemoveId The User that should be removed from the Group.
     */
    public GroupRemoveRequest(User sender, Id groupId, Id userToRemoveId) {
        super();
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(userToRemoveId);

        mSender = sender;
        mGroupId = groupId;
        mUserId = userToRemoveId;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUP_REMOVE;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.groupRemoveRequest(mSender.getId(), mGroupId, mUserId);
    }

    /**
     * Returns the Id of the Group from which the User shall be removed.
     *
     * @return The Id of the Group from which the User shall be removed.
     */
    public Id getGroupId() {
        return mGroupId;
    }

    /**
     * Returns the Id of the User that will be removed from the Group.
     *
     * @return The Id of the User that will be removed from the Group.
     */
    public Id getUserToRemoveId() {
        return mUserId;
    }
}
