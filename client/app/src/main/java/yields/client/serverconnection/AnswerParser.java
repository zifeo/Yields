package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerParser implements ProtocolMessage{
    private final JSONObject object;

    public AnswerParser(String answer) {
        object = new JSONObject();
    }


    @Override
    public JSONObject message() {
        return object;
    }
}
