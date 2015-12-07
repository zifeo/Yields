package yields.client.servicerequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to update the users in the Group (adding or removing).
 */
public class GroupUpdateUsersRequest extends ServiceRequest {

    public enum UpdateType {
        ADD, REMOVE
    }

    private final Id mSender;
    private final Id mGroupId;
    private final List<User> mUsers;
    private final UpdateType mUpdateType;

    /**
     * Main constructor for this type of ServiceRequest (renaming a Group).
     *
     * @param senderId The Id of the User that created this request.
     * @param groupId  The Id of the Group that should be renamed.
     * @param users    The users to add or delete from the group
     */
    public GroupUpdateUsersRequest(Id senderId, Id groupId, List<User> users, UpdateType updateType) {
        super();
        Objects.requireNonNull(senderId);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(updateType);

        mSender = senderId;
        mGroupId = groupId;
        mUsers = users;
        mUpdateType = updateType;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public ServiceRequest.RequestKind getType() {
        return RequestKind.GROUP_UPDATE_USERS;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        List<Id> toRequest = new ArrayList<>();
        for (User user : mUsers) {
            toRequest.add(user.getId());
        }

        switch (mUpdateType) {
            case ADD:
                return RequestBuilder.groupAddRequest(mSender, mGroupId, toRequest);
            case REMOVE:
                return RequestBuilder.groupRemoveRequest(mSender, mGroupId, toRequest);
            default:
                throw new IllegalStateException("no known state : " + mUpdateType);
        }
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
     * Returns the type of the Request, either adding or removing User from a Group.
     *
     * @return The type of the Request, either adding or removing User from a Group
     */
    public UpdateType getUpdateType() {
        return mUpdateType;
    }

    /**
     * Returns the Ids of the Users that will be updated in the Group (either added or removed).
     *
     * @return The Ids of the Users that will be updated in the Group (either added or removed).
     */
    public List<User> getUsersToUpdate() {
        return mUsers;
    }
}