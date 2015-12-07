package yields.client.servicerequest;

import java.util.Date;
import java.util.Objects;

import yields.client.id.Id;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * ServerRequest asking the Service to retrieve Messages from a Group.
 */
public class NodeHistoryRequest extends ServiceRequest {

    public static final int MESSAGE_COUNT = 10;

    private final Id mGroupId;
    private final Date mFurthestDate;

    /**
     * Main constructor for this type of ServiceRequest (retrieving Messages from a Group).
     *
     * @param groupId      The Id of the Group from which the Messages should be retrieved.
     * @param furthestDate The furthest Date from which Messages will be retrieved.
     */
    public NodeHistoryRequest(Id groupId, Date furthestDate) {
        super();

        Objects.requireNonNull(groupId);
        Objects.requireNonNull(furthestDate);

        mFurthestDate = new Date(furthestDate.getTime());
        mGroupId = groupId;
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
                getGroup(), getDate(), MESSAGE_COUNT);
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
     * Getter method for the Id of the Group of this ServiceRequest.
     *
     * @return The Id Group of this ServiceRequest.
     */
    public Id getGroup() {
        return mGroupId;
    }
}
