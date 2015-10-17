package yields.client.serverconnection;

import android.util.ArrayMap;

import org.json.JSONObject;

import java.util.Map;

public class RequestBuilder implements ProtocolMessage {

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

    public RequestBuilder(MessageTypes type) throws RequestBuilderException {
        this.type = type;
        this.constructingMap = new ArrayMap<>();
        constructingMap.put("type", type.name);
    }

    public void addField(Fields fieldType, String field)
            throws RequestBuilderException {

        switch (fieldType) {
            case NAME:
                if(type == MessageTypes.PING ){
                    constructingMap.put("name", field);
                }
                break;
            default: throw new RequestBuilderException("Field doesn't exist");
        }
    }

    @Override
    public JSONObject message() {
        return new JSONObject(constructingMap);
    }
}
