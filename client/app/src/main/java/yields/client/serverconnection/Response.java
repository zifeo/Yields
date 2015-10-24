package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Response{
    private final JSONObject mRawResponse;

    public Response(String rawResponse) throws JSONException {
        Objects.requireNonNull(rawResponse);
        this.mRawResponse = new JSONObject(rawResponse);
    }

    public JSONObject object() {
        return mRawResponse;
    }

    protected String rawResponse(){
        return mRawResponse.toString();
    }
}
