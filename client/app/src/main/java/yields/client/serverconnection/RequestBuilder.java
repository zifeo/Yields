package yields.client.serverconnection;

import android.util.ArrayMap;

import org.json.JSONObject;

import java.util.Map;

import yields.client.id.Id;

public class RequestBuilder {

    public static enum MessageKind {
        PING("PING"), USERCONNECT("UserConnect");

        private final String name;
        MessageKind(String name) { this.name = name; }
        public String getValue() { return name; }
    }
    public static enum Fields {
        EMAIL("email"), CONTENT("content");

        private final String name;
        Fields(String name) { this.name = name; }
        public String getValue() { return name; }
    }

    private final MessageKind kind;
    private final Id sender;
    private final Map<String, Object> constructingMap;

    public static Request userConnectRequest(Id sender, String email) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERCONNECT, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    public static Request simpleRequest(String content) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.PING, new Id(0l));

        builder.addField(Fields.CONTENT, content);

        return builder.request();
    }

    private RequestBuilder(MessageKind kind, Id sender){
        this.kind = kind;
        this.sender = sender;
        this.constructingMap = new ArrayMap<>();
    }

    private void addField(Fields fieldType, String field) {
        this.constructingMap.put(fieldType.getValue(), field);
    }

    private Request request() {
        Map<String, Object> request = new ArrayMap<>();
        request.put("kind", this.kind.name);

        Map<String, Object> metadata = new ArrayMap<>();
        metadata.put("sender", sender.getId().toString());
        metadata.put("time", 0);

        request.put("metadata", new JSONObject(metadata));

        request.put("message", new JSONObject(constructingMap));

        return new Request(new JSONObject(request));
    }
}
