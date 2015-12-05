package yields.client.servicerequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

public class GroupUpdateUsersRequest extends ServiceRequest{


    public enum AddOrRemove {
        ADD, REMOVE
    }

    private final User mSender;
    private final Id mGroupId;
    private final List<User> mUsers;
    private final AddOrRemove mAddOrRemove;

    /**
     * Main constructor for this type of ServiceRequest (renaming a Group).
     *
     * @param sender  The User that created this request.
     * @param groupId The Id of the Group that should be renamed.
     * @param users   The users to add or delete from the group
     */
    public GroupUpdateUsersRequest(User sender, Id groupId, List<User> users, AddOrRemove addOrRemove) {
        super();
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(addOrRemove);

        mSender = sender;
        mGroupId = groupId;
        mUsers = users;
        mAddOrRemove = addOrRemove;
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
        for (User u : mUsers) {
            toRequest.add(u.getId());
        }

        switch (mAddOrRemove) {
            case ADD:
                return RequestBuilder.groupAddRequest(mSender.getId(), mGroupId, toRequest);
            case REMOVE:
                return RequestBuilder.groupRemoveRequest(mSender.getId(), mGroupId, toRequest);
            default:
                throw new IllegalStateException("no known state : " + mAddOrRemove);
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
}
