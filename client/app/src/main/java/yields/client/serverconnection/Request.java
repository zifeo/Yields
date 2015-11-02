package yields.client.serverconnection;

import org.json.JSONObject;

import java.util.Objects;

public class Request{
    private JSONObject mRequest;

    public Request(JSONObject request){
        this.mRequest = Objects.requireNonNull(request);
    }

    public String message() {
        return mRequest.toString();
    }
}
