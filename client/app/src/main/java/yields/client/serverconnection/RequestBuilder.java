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

    protected void addField(Fields fieldType, String field) {

        if(this.kind == MessageKind.PING ){
            constructingMap.put("name", field);
        }
    }

    public Request request() {
        Map<String, Object> request = new ArrayMap<>();
        request.put("kind", this.kind.name);
        request.put("message", new JSONObject(constructingMap));

        Map<String, Object> metadata = new ArrayMap<>();
        metadata.put("time", 0);

        request.put("metadata", new JSONObject(metadata));

        return new Request(new JSONObject(request));
    }
}
