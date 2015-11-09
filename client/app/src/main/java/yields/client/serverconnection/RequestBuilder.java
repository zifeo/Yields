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
import yields.client.node.Group;

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
        GROUPCREATE("GroupCreate"), GROUPUPDATENAME("GroupUpdateName"),
        GROUPUPDATEVISIBILITY("GroupUpdateVisibility"), GROUPUPDATEIMAGE
                ("GroupUpdateImage"),
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
        IMAGE("image"), NID("nid"), VISIBILITY("visibility");

        private final String name;
        Fields(String name) { this.name = name; }
        public String getValue() { return name; }
    }

    private final MessageKind mKind;
    private final Id mSender;
    private final Map<String, Object> mConstructingMap;

    /**
     * Request for updating user properties.
     * @param sender The sender of the request.
     * @param args The properties to be changed organized in a form
     *             property -> new value
     * @return The request.
     */
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

    /**
     * Request to receive the group list.
     * @param sender The sender of the request.
     * @return The request.
     */
    public static Request userGroupListRequest(Id sender) {
        Objects.requireNonNull(sender);
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERGROUPLIST, sender);

        return builder.request();
    }

    /**
     * Request for adding a 'contact' to the user entourage list.
     * @param sender The sender of the request.
     * @param email Email of the new contact to add.
     * @return The request.
     */
    public static Request userEntourageAddRequest(Id sender, String email) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(email);
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERENTOURAGEADD, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    /**
     * Request for removing a 'contact' from the user entourage list.
     * @param sender The sender of the request.
     * @param email Email of the contact to remove.
     * @return The request.
     */
    public static Request userEntourageRemoveRequest(Id sender, String email) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(email);
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERENTOURAGEREMOVE, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    /**
     * Request for connecting a user to the app.
     * @param sender The sender of the request.
     * @param email Email of the user.
     * @return The request.
     */
    public static Request userConnectRequest(Id sender, String email) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(email);
        RequestBuilder builder = new RequestBuilder(
                MessageKind.USERCONNECT, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    /**
     * TODO : Nicolas.C explain please.
     * @param sender The sender of the request.
     * @return The request.
     */
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

    /**
     * Request for updating the group name.
     * @param sender Sender of the request.
     * @param groupId Id of the group having its name changed.
     * @param newName New name for the group.
     * @return The request.
     */
    public static Request GroupUpdateNameRequest(Id sender, Id groupId, String newName){
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newName);

        RequestBuilder builder = new RequestBuilder(MessageKind.GROUPUPDATENAME, sender);
        builder.addField(Fields.GID, groupId);
        builder.addField(Fields.NAME, newName);
        return builder.request();
    }

    /**
     * Request for updating the group visibility.
     * @param sender Sender of the request.
     * @param groupId Id of the group having its name changed.
     * @param newVisibility The new visibility of the group.
     * @return The request.
     */
    public static Request GroupUpdateVisibilityRequest(Id sender, Id groupId,
                                           Group.GroupVisibility newVisibility){
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newVisibility);

        RequestBuilder builder = new RequestBuilder(MessageKind
                .GROUPUPDATEVISIBILITY, sender);
        builder.addField(Fields.GID, groupId);
        builder.addField(Fields.VISIBILITY, newVisibility);
        return builder.request();
    }

    /**
     * Request for updating the group image.
     * @param sender Sender of the reauest.
     * @param groupId Id of the group having its image changed.
     * @param newImage The new Image
     * @return The request.
     */
    public static Request GroupUpdateImageRequest(Id sender, Id groupId,
                                                  ImageContent newImage){
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newImage);

        RequestBuilder builder = new RequestBuilder(MessageKind
                .GROUPUPDATEIMAGE, sender);
        builder.addField(Fields.GID, groupId);
        builder.addField(Fields.IMAGE, newImage.getImage());
        return builder.request();
    }


    /**
     * Request for adding a new user to a group.
     * @param sender The sender of the request.
     * @param groupId Id of the group.
     * @param newUser The user to add in this group.
     * @return The request.
     */
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

    /**
     * Request for removing a user from a group.
     * @param sender The sender of the request.
     * @param groupId Id of the group.
     * @param newUser The user to remove from  this group.
     * @return The request.
     */
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

    /**
     * Constructor of a RequestBuilder
     * @param kind The kind of request to be built.
     * @param sender The sender of the request.
     */
    private RequestBuilder(MessageKind kind, Id sender){
        this.mKind = kind;
        this.mSender = sender;
        this.mConstructingMap = new ArrayMap<>();
    }

    /**
     * Here are the methods allowing us to add fields to the request builder.
     * @param fieldType The type of the field to be added.
     * @param field The value of this field.
     */
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

    private void addField(Fields fieldType, Group.GroupVisibility field){
        this.mConstructingMap.put(fieldType.getValue(), field.toString());
    }

    /**
     * Instantiate the request from the reauest builder.
     * @return The instance of the request.
     */
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

    /**
     * Format a date.
     * @param date The date to be formatted..
     * @return The corresponding formatted format for this date.
     */
    private String formatDate(Date date) {
        Objects.requireNonNull(date);
        return DateSerialization.toString(date);
    }
}
