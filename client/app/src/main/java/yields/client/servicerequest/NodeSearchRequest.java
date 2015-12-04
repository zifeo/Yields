package yields.client.servicerequest;

import yields.client.id.Id;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

public class NodeSearchRequest extends ServiceRequest {

    private final Id mSender;
    private final String mPattern;

    public NodeSearchRequest(Id sender, String pattern) {
        mSender = sender;
        mPattern = pattern;
    }

    @Override
    public RequestKind getType() {
        return RequestKind.NODE_SEARCH;
    }

    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.nodeSearchRequest(mSender, mPattern);
    }
}
