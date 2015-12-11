package yields.client.servicerequest;

import yields.client.id.Id;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

public class GroupInfoRequest extends ServiceRequest {
    private final Id mGroupId;
    private final Id mSender;

    public GroupInfoRequest(Id senderId, Id groupId) {
        mGroupId = groupId;
        mSender = senderId;
    }

    @Override
    public RequestKind getType() {
        return RequestKind.GROUP_INFO;
    }

    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.groupInfoRequest(mSender, mGroupId);
    }
}
