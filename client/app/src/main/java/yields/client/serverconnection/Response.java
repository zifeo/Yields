package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

public class Response{
    private final JSONObject object;

    public Response(String response) {
        object = new JSONObject();
    }

    public JSONObject object() {
        return object;
    }
}
