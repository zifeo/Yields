package yields.client.servicerequest;

import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

public class NodeInfoRequest extends ServiceRequest{
    private final Id mGroupId;
    private final Id mSender;

    public NodeInfoRequest(Id senderId, Id groupId) {
        mGroupId = groupId;
        mSender = senderId;
    }

    @Override
    public RequestKind getType() {
        return RequestKind.GROUP_INFO;
    }

    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.nodeInfoRequest(mSender, mGroupId);
    }
}
