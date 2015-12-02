package yields.client.servicerequest;

import java.util.Date;
import java.util.Objects;

import yields.client.node.Group;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * ServerRequest asking the Service to retrieve Messages from a Group.
 */
public class NodeHistoryRequest extends ServiceRequest {

    public static final int MESSAGE_COUNT = 10;

    private final Group mGroup;
    private final Date mFurthestDate;

    /**
     * Main constructor for this type of ServiceRequest (retrieving Messages from a Group).
     *
     * @param group        The Group from which the Messages should be retrieved.
     * @param furthestDate The furthest Date from which Messages will be retrieved.
     */
    public NodeHistoryRequest(Group group, Date furthestDate) {
        super();

        Objects.requireNonNull(group);
        Objects.requireNonNull(furthestDate);

        mFurthestDate = new Date(furthestDate.getTime());
        mGroup = group;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.NODE_HISTORY;
    }

    /**
     * Build a ServerRequest for sending a message to a group.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.nodeHistoryRequest(YieldsApplication.getUser().getId(),
                getGroup().getId(), getDate(), MESSAGE_COUNT);
    }

    /**
     * Getter method for the furthest Date of this ServiceRequest.
     *
     * @return The furthest Date of this ServiceRequest.
     */
    public Date getDate() {
        return new Date(mFurthestDate.getTime());
    }

    /**
     * Getter method for the Group of this ServiceRequest.
     *
     * @return The Group of this ServiceRequest.
     */
    public Group getGroup() {
        return mGroup;
    }
}