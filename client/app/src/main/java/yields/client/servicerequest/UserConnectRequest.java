package yields.client.servicerequest;

import android.app.Service;

import yields.client.node.ClientUser;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to connect the ClientUser.
 */
public class UserConnectRequest extends ServiceRequest {

    private final ClientUser mClientUser;

    /**
     * Main constructor for this type of ServiceRequest (connecting to server).
     *
     * @param clientUser The ClientUser sending the request.
     */
    public UserConnectRequest(ClientUser clientUser) {
        super();
        mClientUser = clientUser;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.USERCONNECT;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.userConnectRequest(mClientUser.getId(), mClientUser.getEmail());
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {

    }
}
