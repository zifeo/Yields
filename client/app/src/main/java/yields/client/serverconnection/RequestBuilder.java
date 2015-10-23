package yields.client.serverconnection;

import android.graphics.Bitmap;
import android.util.ArrayMap;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import yields.client.id.Id;
import yields.client.messages.ImageContent;

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
        LAST("last"), TO("to"), COUNT("count"),
        IMAGE("image"), NID("nid"), HOWMANY("how-many");

        private final String name;
        Fields(String name) { this.name = name; }
        public String getValue() { return name; }
    }

    private final MessageKind mKind;
    private final Id mSender;
    private final Map<String, Object> mConstructingMap;

    public static Request UserUpdateRequest(Id sender, Map<Fields, String> args) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(args);
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
        Objects.requireNonNull(sender);
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERGROUPLIST, sender);

        return builder.request();
    }

    public static Request userEntourageAddRequest(Id sender, String email) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(email);
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERENTOURAGEADD, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    public static Request userEntourageRemoveRequest(Id sender, String email) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(email);
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERENTOURAGEREMOVE, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    public static Request userConnectRequest(Id sender, String email) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(email);
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERCONNECT, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    public static Request userUpdateRequest(Id sender) {
        Objects.requireNonNull(sender);
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERSTATUS, sender);

        return builder.request();
    }

    public static Request GroupCreateRequest(Id sender, String name,
                                             List<Id> nodes) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(name);
        Objects.requireNonNull(nodes);

        if(nodes.size() < 1) {
            throw new IllegalArgumentException();
        }

        RequestBuilder builder = new RequestBuilder(
                MessageKind.GROUPCREATE, sender);

        builder.addField(Fields.NAME, name);
        builder.addField(Fields.NODES, nodes);

        return builder.request();
    }

    public static Request GroupUpdateRequest(Id sender, Id groupId,
                                             String newName,
                                             ImageContent newImage) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);

        if (newName == null && newImage == null) {
            throw new IllegalArgumentException();
        }

        RequestBuilder builder = new RequestBuilder(
                MessageKind.GROUPUPDATE, sender);

        builder.addField(Fields.GID, groupId);

        if (newName != null) {
            builder.addField(Fields.NAME, newName);
        }
        if (newImage != null) {
            builder.addField(Fields.IMAGE, newImage.getImage());
        }

        return builder.request();
    }


    public static Request GroupAddRequest(Id sender, Id groupId,
                                          Id newUser) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newUser);

        RequestBuilder builder = new RequestBuilder(
                MessageKind.GROUPADD, sender);

        builder.addField(Fields.GID, groupId);
        builder.addField(Fields.NID, newUser);

        return builder.request();
    }

    public static Request GroupRemoveRequest(Id sender, Id groupId,
                                             Id newUser ) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newUser);

        RequestBuilder builder = new RequestBuilder(
                MessageKind.GROUPREMOVE, sender);

        builder.addField(Fields.GID, groupId);
        builder.addField(Fields.NID, newUser);

        return builder.request();
    }

    public static Request GroupMessageRequest(Id sender, Id groupId,
                                              String kind, String content) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(kind);
        Objects.requireNonNull(content);

        RequestBuilder builder = new RequestBuilder(
                MessageKind.GROUPMESSAGE, sender);

        builder.addField(Fields.GID, groupId);
        builder.addField(Fields.KIND, kind);
        builder.addField(Fields.CONTENT, content);

        return builder.request();
    }

    public static Request GroupHistoryRequest(Id sender, Id last,
                                              int messageCount) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(last);
        Objects.requireNonNull(messageCount);

        RequestBuilder builder = new RequestBuilder(
                MessageKind.GROUPHISTORY, sender);

        builder.addField(Fields.LAST, last);
        builder.addField(Fields.COUNT, last);

        return builder.request();
    }

    public static Request pingRequest(String content) {
        Objects.requireNonNull(content);

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

    private void addField(Fields fieldType, Bitmap field) {
        int size     = field.getRowBytes() * field.getHeight();
        ByteBuffer b = ByteBuffer.allocate(size);

        field.copyPixelsToBuffer(b);

        byte[] byteImage = b.array();
        this.mConstructingMap.put(fieldType.getValue(), Base64
                .encodeToString(byteImage, Base64.DEFAULT));
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
