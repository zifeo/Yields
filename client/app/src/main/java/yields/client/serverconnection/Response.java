package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

public class Response{
    private final String mRawResponse;

    public Response(String rawResponse) {
        this.mRawResponse = rawResponse;
    }

    public JSONObject object() throws JSONException {
        return new JSONObject(mRawResponse);
    }

    protected String rawResponse(){
        return mRawResponse;
    }
}
