package yields.client.servicerequest;

import android.app.Service;

import java.util.Objects;

import yields.client.node.ClientUser;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to ping the Server.
 */
public class PingRequest extends ServiceRequest {

    private final ClientUser mClientUser;
    private final String mContent;

    /**
     * Main constructor for this type of ServiceRequest (pinging the server).
     *
     * @param clientUser  The ClientUser sending the ping.
     * @param pingContent The content, a String, added to the ping.
     */
    public PingRequest(ClientUser clientUser, String pingContent) {
        super();
        Objects.requireNonNull(clientUser);
        Objects.requireNonNull(pingContent);

        mClientUser = clientUser;
        mContent = pingContent;
    }

    /**
     * Returns the type of this ServiceRequest as a String.
     *
     * @return The type of this ServiceRequest as a String.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.PING;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.pingRequest(mClientUser.getId(), mContent);
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {

    }
}
