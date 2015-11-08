package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The response object which represents a response to be send to the server.
 */
public class Response{
    private final JSONObject mRawResponse;

    /**
     * Creates a response as a Json Object
     *
     * @param rawResponse The String of the response
     * @throws JSONException In case of problems parsing the response
     */
    public Response(String rawResponse) throws JSONException {
        //Objects.requireNonNull(rawResponse);
        this.mRawResponse = new JSONObject();
    }

    /**
     * Gets the Json Object of the response
     *
     * @return The Json Object
     */
    public JSONObject object() {
        return mRawResponse;
    }

    protected String rawResponse(){
        return mRawResponse.toString();
    }
}
