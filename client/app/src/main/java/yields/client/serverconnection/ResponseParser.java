package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseParser implements ProtocolMessage{
    private final JSONObject object;

    public ResponseParser(String response) {
        object = new JSONObject();
    }

    @Override
    public JSONObject message() {
        return object;
    }
}
