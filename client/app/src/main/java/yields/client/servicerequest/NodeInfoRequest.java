package yields.client.servicerequest;

import yields.client.id.Id;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * Creates a ServiceRequest for retrieving information about a Node.
 */
public class NodeInfoRequest extends ServiceRequest {

    private final Id mGroupId;
    private final Id mSender;

    /**
     * Main constructor for this type of ServiceRequest (creating a RSS).
     *
     * @param senderId The Id of the sender of the request.
     * @param groupId  The Id of the node about which information shall be retrieved.
     */
    public NodeInfoRequest(Id senderId, Id groupId) {
        mGroupId = groupId;
        mSender = senderId;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.NODE_INFO;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.nodeInfoRequest(mSender, mGroupId);
    }
}
