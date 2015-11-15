package yields.client.servicerequest;

import android.app.Service;

import java.util.Date;

import yields.client.id.Id;
import yields.client.serverconnection.ServerRequest;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;

/**
 * ServerRequest asking the Service to retrieve Messages from a Group.
 */
public class GroupHistoryRequest extends ServiceRequest {

    public static final int MESSAGE_COUNT = 10;

    private final Id mGroupId;
    private final Date mFurthestDate;

    /**
     * Main constructor for this type of ServiceRequest (retrieving Messages from a Group).
     *
     * @param groupId      The Id of the Group from which the Messages should be retrieved.
     * @param furthestDate The furthest Date from which Messages will be retrieved.
     */
    public GroupHistoryRequest(Id groupId, Date furthestDate) {
        super();
        mFurthestDate = new Date(furthestDate.getTime());
        mGroupId = groupId;
    }

    /**
     * Returns the type of this ServiceRequest as a String.
     *
     * @return The type of this ServiceRequest as a String.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUPHISTORY;
    }

    /**
     * Build a ServerRequest for sending a message to a group.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.GroupHistoryRequest(getGroupId(), getDate(), MESSAGE_COUNT);
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {
        //TODO : @Trofleb
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
     * @return The Id of the Group of this ServiceRequest.
     */
    public Id getGroupId() {
        return mGroupId;
    }
}
