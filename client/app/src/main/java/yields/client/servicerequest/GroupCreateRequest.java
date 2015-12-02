package yields.client.servicerequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to create a Group.
 */
public class GroupCreateRequest extends ServiceRequest {

    private final User mCreator;
    private final Group mGroup;

    /**
     * Main constructor for this type of ServiceRequest (creating a Group).
     *
     * @param creator The User that created the Group.
     * @param group   The Group that should be created.
     */
    public GroupCreateRequest(User creator, Group group) {
        super();
        Objects.requireNonNull(creator);
        Objects.requireNonNull(group);

        mCreator = creator;
        mGroup = group;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUP_CREATE;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        List<Id> userIds = mGroup.getUsers();
        List<Id> nodeIds = new ArrayList<>();

        return RequestBuilder.groupCreateRequest(mCreator.getId(), mGroup.getName(), mGroup
                .getVisibility(), userIds, nodeIds);
    }

    /**
     * Returns the Group that will be created.
     *
     * @return The Group that will be created.
     */
    public Group getGroup() {
        return mGroup;
    }

}
