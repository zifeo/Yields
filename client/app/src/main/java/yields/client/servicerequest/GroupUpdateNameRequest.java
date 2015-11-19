package yields.client.servicerequest;

import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to rename a Group.
 */
public class GroupUpdateNameRequest extends ServiceRequest {

    private final User mSender;
    private final Id mGroupId;
    private final String mName;

    /**
     * Main constructor for this type of ServiceRequest (renaming a Group).
     *
     * @param sender  The User that created this request.
     * @param groupId The Id of the Group that should be renamed.
     * @param name    The new name of the Group.
     */
    public GroupUpdateNameRequest(User sender, Id groupId, String name) {
        super();
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(name);

        mSender = sender;
        mGroupId = groupId;
        mName = name;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUP_UPDATE_NAME;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.groupUpdateNameRequest(mSender.getId(), mGroupId, mName);
    }

    /**
     * Returns the Id of the Group that will be renamed.
     *
     * @return The Id of the Group that will be renamed.
     */
    public Id getGroupId() {
        return mGroupId;
    }

    /**
     * Returns the new name of the Group.
     *
     * @return The new name of the Group.
     */
    public String getNewGroupName() {
        return mName;
    }
}
