package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Response{
    private final String mRawResponse;

    public Response(String rawResponse) {
        Objects.requireNonNull(rawResponse);
        this.mRawResponse = rawResponse;
    }

    public JSONObject object() throws JSONException {
        return new JSONObject(mRawResponse);
    }

    protected String rawResponse(){
        return mRawResponse;
    }
}
