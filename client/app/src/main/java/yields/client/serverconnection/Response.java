package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

public class Response{
    private final String rawResponse;

    public Response(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public JSONObject object() throws JSONException {
        return new JSONObject(rawResponse);
    }
}
