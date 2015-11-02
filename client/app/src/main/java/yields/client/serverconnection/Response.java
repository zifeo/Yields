package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import yields.client.messages.Message;

public class Response{
    private final JSONObject mRawResponse;

    public Response(String rawResponse) throws JSONException {
        //Objects.requireNonNull(rawResponse);
        this.mRawResponse = new JSONObject();
    }

    public JSONObject object() {
        return mRawResponse;
    }

    protected String rawResponse(){
        return mRawResponse.toString();
    }
}
