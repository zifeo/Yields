package yields.client.serverconnection;

import android.util.ArrayMap;

import org.json.JSONObject;

import java.util.Map;

public class RequestBuilder {

    public static enum MessageTypes {
        PING("PING");

        private final String name;
        MessageTypes(String name) { this.name = name; }
        public String getValue() { return name; }
    }
    public static enum Fields {
        NAME
    }

    private final MessageTypes type;
    private final Map<String, Object> constructingMap;

    public RequestBuilder(MessageTypes type){
        this.type = type;
        this.constructingMap = new ArrayMap<>();
    }

    public void addField(Fields fieldType, String field) {

        switch (fieldType) {
            case NAME:
                if(type == MessageTypes.PING ){
                    constructingMap.put("name", field);
                }
                break;
            default: throw new IllegalArgumentException();
        }
    }

    public Request request() {
        Map<String, Object> object = new ArrayMap<>();
        object.put("type", type.name);
        object.put("time", 0);
        object.put("hash", 0);
        object.put("message", new JSONObject(constructingMap));
        return new Request(new JSONObject(object));
    }
}
