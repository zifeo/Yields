package yields.client.servicerequest;

import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to update a Group's visibility.
 */
public class GroupUpdateVisibilityRequest extends ServiceRequest {

    private final User mSender;
    private final Id mGroupId;
    private final Group.GroupVisibility mVisibility;

    /**
     * Main constructor for this type of ServiceRequest (updating a Group's visibility).
     *
     * @param sender     The User that created this request.
     * @param groupId    The Id of the Group that should have it's visibility changed.
     * @param visibility The new visibility of the Group
     */
    public GroupUpdateVisibilityRequest(User sender, Id groupId, Group.GroupVisibility visibility) {
        super();
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(visibility);

        mSender = sender;
        mGroupId = groupId;
        mVisibility = visibility;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUP_UPDATE_VISIBILITY;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.groupUpdateVisibilityRequest(mSender.getId(), mGroupId,
                mVisibility);
    }

    /**
     * Returns the new Group.GroupVisibility of the Group.
     *
     * @return The new Group.GroupVisibility of the Group.
     */
    public Group.GroupVisibility getNewGroupVisibility() {
        return mVisibility;
    }

    /**
     * Returns the Id of the Group that will have it's Group.GroupVisibility updated.
     *
     * @return The Id of the Group that will have it's Group.GroupVisibility updated.
     */
    public Id getGroupId() {
        return mGroupId;
    }
}
