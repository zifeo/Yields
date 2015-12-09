package yields.client.servicerequest;

import yields.client.id.Id;
import yields.client.node.Node;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

public class RSSCreateRequest extends ServiceRequest {

    private final Id mSender;
    private final String mUrl;
    private final Node mNode;
    private final String mFilter;

    public RSSCreateRequest(Id sender, String url, Node node, String filter) {
        mUrl = url;
        mNode = node;
        mFilter = filter;
        mSender = sender;
    }

    @Override
    public RequestKind getType() {
        return null;
    }

    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.rssCreateRequest(mSender, mNode, mUrl, mFilter);
    }
}
