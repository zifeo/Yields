package yields.client.serverconnection;

import android.util.ArrayMap;

import org.json.JSONObject;

import java.util.Map;

public class RequestBuilder {

    public static enum MessageKind {
        PING("PING");

        private final String name;
        MessageKind(String name) { this.name = name; }
        public String getValue() { return name; }
    }
    public static enum Fields {
        NAME
    }

    private final MessageKind kind;
    private final Map<String, Object> constructingMap;

    public RequestBuilder(MessageKind kind){
        this.kind = kind;
        this.constructingMap = new ArrayMap<>();
    }

    private void addField(Fields fieldType, String field) {

        if(this.kind == MessageKind.PING ){
            constructingMap.put("name", field);
        }
    }

    public Request request() {
        Map<String, Object> object = new ArrayMap<>();
        object.put("kind", this.kind.name);
        object.put("time", 0);
        object.put("hash", 0);
        object.put("message", new JSONObject(constructingMap));
        return new Request(new JSONObject(object));
    }
}
