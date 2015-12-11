package yields.client.servicerequest;

import yields.client.id.Id;
import yields.client.node.Node;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * Creates a ServiceRequest for creating a RSS.
 */
public class RSSCreateRequest extends ServiceRequest {

    private final Id mSender;
    private final String mUrl;
    private final Node mNode;
    private final String mFilter;

    /**
     * Main constructor for this type of ServiceRequest (creating a RSS).
     *
     * @param sender The Id of the sender of the Request.
     * @param url    The url of the RSS feed.
     * @param node   The RSS as a Node.
     * @param filter A filter for the RSS feed.
     */
    public RSSCreateRequest(Id sender, String url, Node node, String filter) {
        mUrl = url;
        mNode = node;
        mFilter = filter;
        mSender = sender;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.RSS_CREATE;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.rssCreateRequest(mSender, mNode, mUrl, mFilter);
    }
}
