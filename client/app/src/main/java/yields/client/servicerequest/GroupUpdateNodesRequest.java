package yields.client.servicerequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

public class GroupUpdateNodesRequest extends ServiceRequest {

    public enum UpdateType {
        ADD, REMOVE
    }

    private final Id mSender;
    private final Id mGroupId;
    private final List<Group> mGroups;
    private final UpdateType mUpdateType;

    /**
     * Main constructor for this type of ServiceRequest (renaming a Group).
     *
     * @param senderId The Id of the User that created this request.
     * @param groupId  The Id of the Group that should be renamed.
     * @param groups   The users to add or delete from the group
     */
    public GroupUpdateNodesRequest(Id senderId, Id groupId, List<Group> groups,
                                   GroupUpdateNodesRequest.UpdateType updateType) {
        super();
        Objects.requireNonNull(senderId);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(updateType);

        mSender = senderId;
        mGroupId = groupId;
        mGroups = groups;
        mUpdateType = updateType;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public ServiceRequest.RequestKind getType() {
        return RequestKind.GROUP_UPDATE_NODES;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        List<Id> toRequest = new ArrayList<>();
        for (Group group : mGroups) {
            toRequest.add(group.getId());
        }

        switch (mUpdateType) {
            case ADD:
                return RequestBuilder.groupAddNodesRequest(mSender, mGroupId, toRequest);
            case REMOVE:
                return RequestBuilder.groupRemoveNodesRequest(mSender, mGroupId, toRequest);
            default:
                throw new IllegalStateException("no known state : " + mUpdateType);
        }
    }
}