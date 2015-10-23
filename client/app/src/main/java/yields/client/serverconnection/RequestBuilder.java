package yields.client.serverconnection;

import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

import yields.client.id.Id;

public class RequestBuilder {

    private enum MessageKind {
        PING("PING"), USERCONNECT("UserConnect"), USERUPDATE("UserUpdate"),
        USERGROUPLIST("UserGroupList"), USERENTOURAGEADD("UserEntourageAdd"),
        USERENTOURAGEREMOVE("UserEntourageRemove"), USERSTATUS("UserStatus"),
        GROUPCREATE("GroupCreate"), GROUPUPDATE("GroupUpdate"),
        GROUPADD("GroupAdd"), GROUPREMOVE("GroupRemove"),
        GROUPMESSAGE("GroupMessage"), GROUPHISTORY("GroupHistory");

        private final String name;
        MessageKind(String name) { this.name = name; }
        public String getValue() { return name; }
    }
    public enum Fields {
        EMAIL("email"), CONTENT("content"), NAME("name"),
        NODES("nodes"), GID("gid"), KIND("kind"),
        FROM("from"), TO("to"), COUNT("count");

        private final String name;
        Fields(String name) { this.name = name; }
        public String getValue() { return name; }
    }

    private final MessageKind mKind;
    private final Id mSender;
    private final Map<String, Object> mConstructingMap;

    public static Request UserUpdateRequest(Id sender, Map<Fields, String> args) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERUPDATE, sender);

        if (args.containsKey(Fields.EMAIL)) {
            builder.addField(Fields.EMAIL, args.get(Fields.EMAIL));
        }
        if (args.containsKey(Fields.NAME)) {
            builder.addField(Fields.NAME, args.get(Fields.NAME));
        }
        /* PIC if (args.containsKey(Fields.EMAIL)) {
            builder.addField(Fields.EMAIL, args.get(Fields.EMAIL));
        }*/

        return builder.request();
    }

    public static Request userGroupListRequest(Id sender) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERGROUPLIST, sender);

        return builder.request();
    }

    public static Request userEntourageAddRequest(Id sender, String email) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERENTOURAGEADD, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    public static Request userEntourageRemoveRequest(Id sender, String email) {

        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERENTOURAGEREMOVE, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    public static Request userConnectRequest(Id sender, String email) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERCONNECT, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    public static Request userUpdateRequest(Id sender) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERSTATUS, sender);

        return builder.request();
    }

    public static Request GroupCreateRequest(Id sender, String name,
                                             List<Id> nodes) {

        RequestBuilder builder = new RequestBuilder(
                MessageKind.GROUPCREATE, sender);

        builder.addField(Fields.NAME, name);
        builder.addField(Fields.NODES, nodes);

        return builder.request();
    }

    public static Request GroupUpdateRequest(Id sender, Id groupId,
                                             Map<Fields, String> args) {

        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERUPDATE, sender);

        builder.addField(Fields.GID, groupId);

        if (args.containsKey(Fields.NAME)) {
            builder.addField(Fields.NAME, args.get(Fields.NAME));
        }
        /* PIC if (args.containsKey(Fields.EMAIL)) {
            builder.addField(Fields.EMAIL, args.get(Fields.EMAIL));
        }*/

        return builder.request();
    }


    public static Request GroupAddRequest(Id sender, Map<Fields, String> args) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERUPDATE, sender);

        if (args.containsKey(Fields.EMAIL)) {
            builder.addField(Fields.EMAIL, args.get(Fields.EMAIL));
        }
        if (args.containsKey(Fields.NAME)) {
            builder.addField(Fields.NAME, args.get(Fields.NAME));
        }
        /* PIC if (args.containsKey(Fields.EMAIL)) {
            builder.addField(Fields.EMAIL, args.get(Fields.EMAIL));
        }*/

        return builder.request();
    }

    public static Request GroupRemoveRequest(Id sender, Map<Fields, String> args) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERUPDATE, sender);

        if (args.containsKey(Fields.EMAIL)) {
            builder.addField(Fields.EMAIL, args.get(Fields.EMAIL));
        }
        if (args.containsKey(Fields.NAME)) {
            builder.addField(Fields.NAME, args.get(Fields.NAME));
        }
        /* PIC if (args.containsKey(Fields.EMAIL)) {
            builder.addField(Fields.EMAIL, args.get(Fields.EMAIL));
        }*/

        return builder.request();
    }

    public static Request GroupMessageRequest(Id sender, Id groupId,
                                              String kind, String content) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.GROUPMESSAGE, sender);

        builder.addField(Fields.GID, groupId);
        builder.addField(Fields.KIND, kind);
        builder.addField(Fields.CONTENT, content);

        return builder.request();
    }

    public static Request GroupHistoryRequest(Id sender, Date from) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERUPDATE, sender);

        builder.addField(Fields.FROM, from);

        return builder.request();
    }

    public static Request GroupHistoryRequest(Id sender, int messageCount) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERUPDATE, sender);

        builder.addField(Fields.COUNT, messageCount);

        return builder.request();
    }

    public static Request GroupHistoryRequest(Id sender, Date from, Date to) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERUPDATE, sender);

        builder.addField(Fields.FROM, from);
        builder.addField(Fields.TO, to);

        return builder.request();
    }

    public static Request simpleRequest(String content) {
        RequestBuilder builder = new RequestBuilder(
                MessageKind.PING, new Id(0l));

        builder.addField(Fields.CONTENT, content);

        return builder.request();
    }

    private RequestBuilder(MessageKind kind, Id sender){
        this.mKind = kind;
        this.mSender = sender;
        this.mConstructingMap = new ArrayMap<>();
    }

    private void addField(Fields fieldType, String field) {
        this.mConstructingMap.put(fieldType.getValue(), field);
    }

    private void addField(Fields fieldType, List field) {
        this.mConstructingMap.put(fieldType.getValue(), new JSONArray(field));
    }

    private void addField(Fields fieldType, Id field) {
        this.mConstructingMap.put(fieldType.getValue(), field.getId());
    }

    private void addField(Fields fieldType, Date field) {
        this.mConstructingMap.put(fieldType.getValue(), field.toString());
    }

    private void addField(Fields fieldType, int field) {
        this.mConstructingMap.put(fieldType.getValue(), field);
    }

    private Request request() {
        Map<String, Object> request = new ArrayMap<>();
        request.put("kind", mKind.name);

        Map<String, Object> metadata = new ArrayMap<>();
        metadata.put("sender", mSender.getId());
        metadata.put("time", 0);

        request.put("metadata", new JSONObject(metadata));

        request.put("message", new JSONObject(mConstructingMap));

        return new Request(new JSONObject(request));
    }
}
