package yields.client.serverconnection;

import android.graphics.Bitmap;
import android.util.ArrayMap;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import yields.client.id.Id;
import yields.client.messages.ImageContent;

/**
 * A builder for requests that will be send to the server
 */
public class RequestBuilder {

    /**
     * The Kind of messages possible
     */
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

    /**
     * The Fields possible for the request
     */
    public enum Fields {
        EMAIL("email"), CONTENT("content"), NAME("name"),
        NODES("nodes"), GID("gid"), KIND("kind"),
        LAST("dateLast"), TO("to"), COUNT("count"),
        IMAGE("image"), NID("nid");

        private final String name;
        Fields(String name) { this.name = name; }
        public String getValue() { return name; }
    }

    private final MessageKind mKind;
    private final Id mSender;
    private final Map<String, Object> mConstructingMap;

    public static Request UserUpdateRequest(Id sender,
                                            Map<Fields, String> args) {
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

    /**
     * Creates a Group create request
     *
     * @param sender The id of the sender
     * @param name The new name of the group
     * @param nodes The nodes attached to the group
     * @return The request itself
     */
    public static Request GroupCreateRequest(Id sender, String name,
                                             List<Id> nodes) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(name);
        Objects.requireNonNull(nodes);

        if(nodes.size() < 1) {
            throw new IllegalArgumentException("No nodes to add...");
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
            throw new IllegalArgumentException(
                    "You make no change with this group update");
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

    /**
     * Creates a Group message request
     *
     * @param sender The id of the sender
     * @param groupId The group id to send the message to
     * @param kind The kind of the message should be text
     * @param content The content of the message
     * @return The request itself
     */
    public static Request GroupTextMessageRequest(Id sender, Id groupId,
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

    /**
     * Creates a Group image message request
     * @param sender The id of the sender
     * @param groupId The group id of the recipient
     * @param kind The kind of the message should be image
     * @param content The image to send
     * @return The request itself
     */
    public static Request GroupImageMessageRequest(Id sender, Id groupId,
                                                   String kind,
                                                   ImageContent content) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(kind);
        Objects.requireNonNull(content);

        RequestBuilder builder = new RequestBuilder(
                MessageKind.GROUPMESSAGE, sender);

        builder.addField(Fields.GID, groupId);
        builder.addField(Fields.KIND, kind);
        builder.addField(Fields.IMAGE, content.getImage());

        return builder.request();
    }

    /**
     * Creates a group history request
     *
     * @param groupId The id of the group you want the history from
     * @param last The last time we got a message from this group
     * @param messageCount The max number of message we want
     * @return The request itself
     */
    public static Request GroupHistoryRequest(Id groupId, Date last,
                                              int messageCount) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(last);
        Objects.requireNonNull(messageCount);

        RequestBuilder builder = new RequestBuilder(
                MessageKind.GROUPHISTORY, groupId);

        builder.addField(Fields.LAST, last);
        builder.addField(Fields.COUNT, messageCount);

        return builder.request();
    }

    /**
     * Creates a simple ping request
     *
     * @param content A string of content
     * @return The request itself
     */
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

    private void addField(Fields fieldType, int field) {
        this.mConstructingMap.put(fieldType.getValue(), field);
    }

    private void addField(Fields fieldType, Date field) {
        this.mConstructingMap.put(fieldType.getValue(),
                formatDate(field));
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

        metadata.put("datetime", formatDate(
                new Date()));

        request.put("metadata", new JSONObject(metadata));

        request.put("message", new JSONObject(mConstructingMap));

        return new Request(new JSONObject(request));
    }

    private String formatDate(Date date) {

        return DateSerialization.toString(date);
    }
}
