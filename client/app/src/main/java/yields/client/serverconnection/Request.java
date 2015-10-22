package yields.client.serverconnection;

import org.json.JSONObject;

public class Request{
    private JSONObject mRequest;

    public Request(JSONObject request){
        this.mRequest = request;
    }

    public String message() {
        return mRequest.toString();
    }
}
