package yields.client.serverconnection;

import org.json.JSONObject;

public class Request{
    JSONObject request;

    public Request(JSONObject request){
        this.request = request;
    }

    public String message() {
        return request.toString();
    }
}
