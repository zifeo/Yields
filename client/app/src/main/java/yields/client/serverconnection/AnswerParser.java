package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerParser {
    String type;

    public AnswerParser(JSONObject answer) {
        if (answer == null || answer.has("type")) {
            throw new IllegalArgumentException();
        }

        try {
            type = (String) answer.get("type");
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
