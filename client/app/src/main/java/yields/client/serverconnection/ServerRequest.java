package yields.client.serverconnection;

import org.json.JSONObject;

import java.util.Objects;

/**
 * The request object which represents a request to be send to the server.
 */
public class ServerRequest {
    private JSONObject mRequest;

    /**
     * Creates a request thanks to a Json Object
     *
     * @param request The ServerRequest in Json format
     */
    public ServerRequest(JSONObject request) {
        this.mRequest = Objects.requireNonNull(request);
    }

    /**
     * The string representation of the request
     *
     * @return A string representing the request
     */
    public String message() {
        return mRequest.toString();
    }
}
