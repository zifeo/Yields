package yields.client.serverconnection;

import android.graphics.Bitmap;
import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import yields.client.exceptions.ContentException;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.servicerequest.ServiceRequest;

/**
 * A builder for requests that will be send to the server
 */
public class RequestBuilder {

    /**
     * The Fields possible for the request
     */
    public enum Fields {
        EMAIL("email"), TEXT("content"), NAME("name"),
        NODES("nodes"), KIND("kind"),
        LAST("datetime"), TO("to"), COUNT("count"),
        IMAGE("image"), NID("nid"), VISIBILITY("visibility"),
        CONTENT_TYPE("contentType");

        private final String name;

        Fields(String name) {
            this.name = name;
        }

        public String getValue() {
            return name;
        }
    }

    private final ServiceRequest.RequestKind mKind;
    private final Id mSender;
    private final Map<String, Object> mConstructingMap;

    /**
     * ServerRequest for updating user properties.
     *
     * @param sender The sender of the request.
     * @param args   The properties to be changed organized in a form
     *               property -> new value
     * @return The request.
     */
    public static ServerRequest userUpdateRequest(Id sender,
                                                  Map<Fields, String> args) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(args);
        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.USER_UPDATE, sender);

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
     * ServerRequest to receive the group list.
     *
     * @param senderId The senderId of the request.
     * @return The request.
     */
    public static ServerRequest userGroupListRequest(Id senderId) {
        Objects.requireNonNull(senderId);
        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.USER_GROUP_LIST, senderId);

        return builder.request();
    }

    /**
     * ServerRequest for adding a 'contact' to the user entourage list.
     *
     * @param sender The sender of the request.
     * @param email  Email of the new contact to add.
     * @return The request.
     */
    public static ServerRequest userEntourageAddRequest(Id sender, String email) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(email);
        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.USER_ENTOURAGE_ADD, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    /**
     * ServerRequest for removing a 'contact' from the user entourage list.
     *
     * @param sender The sender of the request.
     * @param email  Email of the contact to remove.
     * @return The request.
     */
    public static ServerRequest userEntourageRemoveRequest(Id sender, String email) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(email);
        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.USER_ENTOURAGE_REMOVE, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    /**
     * ServerRequest for connecting a user to the app.
     *
     * @param sender The sender of the request.
     * @param email  Email of the user.
     * @return The request.
     */
    public static ServerRequest userConnectRequest(Id sender, String email) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(email);
        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.USER_CONNECT, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    /**
     * Update the user info on server
     *
     * @param sender The sender of the request.
     * @return The request.
     */
    public static ServerRequest userUpdateRequest(Id sender) {
        Objects.requireNonNull(sender);
        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.USER_STATUS, sender);

        return builder.request();
    }

    /**
     * Creates a Group create request
     *
     * @param sender     The id of the sender
     * @param name       The new name of the group
     * @param visibility The visibility of the group.
     * @param nodes      The nodes attached to the group
     * @return The request itself
     */
    public static ServerRequest groupCreateRequest(Id sender, String name,
                                                   Group.GroupVisibility visibility,
                                                   List<Id> nodes) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(name);
        Objects.requireNonNull(nodes);

        if (nodes.size() < 1) {
            throw new IllegalArgumentException("No nodes to add...");
        }
        List<String> nodeIds = new ArrayList<>();
        for (Id id : nodes) {
            nodeIds.add(id.getId());
        }

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.GROUP_CREATE, sender);

        builder.addField(Fields.NAME, name);
        builder.addField(Fields.NODES, nodeIds);
        builder.addField(Fields.VISIBILITY, visibility);

        return builder.request();
    }

    /**
     * ServerRequest for updating the group name.
     *
     * @param sender  Sender of the request.
     * @param groupId Id of the group having its name changed.
     * @param newName New name for the group.
     * @return The request.
     */
    public static ServerRequest groupUpdateNameRequest(Id sender, Id groupId, String newName) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newName);

        RequestBuilder builder = new RequestBuilder(ServiceRequest.RequestKind.GROUP_UPDATE_NAME, sender);
        builder.addField(Fields.GID, groupId);
        builder.addField(Fields.NAME, newName);
        return builder.request();
    }

    /**
     * ServerRequest for updating the group visibility.
     *
     * @param sender        Sender of the request.
     * @param groupId       Id of the group having its name changed.
     * @param newVisibility The new visibility of the group.
     * @return The request.
     */
    public static ServerRequest groupUpdateVisibilityRequest(Id sender, Id groupId,
                                                             Group.GroupVisibility newVisibility) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newVisibility);

        RequestBuilder builder = new RequestBuilder(ServiceRequest.RequestKind
                .GROUP_UPDATE_VISIBILITY, sender);
        builder.addField(Fields.GID, groupId);
        builder.addField(Fields.VISIBILITY, newVisibility);

        return builder.request();
    }

    /**
     * ServerRequest for updating the group image.
     *
     * @param sender   Sender of the request.
     * @param groupId  Id of the group having its image changed.
     * @param newImage The new Image
     * @return The request.
     */
    public static ServerRequest groupUpdateImageRequest(Id sender, Id groupId,
                                                        Bitmap newImage) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newImage);

        RequestBuilder builder = new RequestBuilder(ServiceRequest.RequestKind
                .GROUP_UPDATE_IMAGE, sender);
        builder.addField(Fields.NID, groupId);
        builder.addField(Fields.IMAGE, newImage);
        return builder.request();
    }


    /**
     * ServerRequest for adding a new user to a group.
     *
     * @param sender  The sender of the request.
     * @param groupId Id of the group.
     * @param newUser The user to add in this group.
     * @return The request.
     */
    public static ServerRequest groupAddRequest(Id sender, Id groupId,
                                                Id newUser) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newUser);

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.GROUP_ADD, sender);

        builder.addField(Fields.NID, groupId);
        builder.addField(Fields.NID, newUser);

        return builder.request();
    }

    /**
     * ServerRequest for removing a user from a group.
     *
     * @param sender       The sender of the request.
     * @param groupId      Id of the group.
     * @param userToRemove The user to remove from  this group.
     * @return The request.
     */
    public static ServerRequest groupRemoveRequest(Id sender, Id groupId,
                                                   Id userToRemove) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(userToRemove);

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.GROUP_REMOVE, sender);

        builder.addField(Fields.NID, groupId);
        builder.addField(Fields.NID, userToRemove);

        return builder.request();
    }

    /**
     * Creates a Group message request for a Message (no matter what it's Content is).
     *
     * @param sender  The Id of the sender.
     * @param groupId The Id of the group to which the Message is sent to.
     * @param content The Content of the Message that is sent.
     * @return The request itself.
     */
    public static ServerRequest groupMessageRequest(Id sender, Id groupId, Content content) {
        switch (content.getType()) {
            case TEXT:
                return groupTextMessageRequest(sender, groupId, (TextContent) content);
            case IMAGE:
                return groupImageMessageRequest(sender, groupId, (ImageContent) content);
            default:
                throw new ContentException("No such ContentType exists !");
        }
    }

    /**
     * Creates a Group message request for a Message that has a TextContent.
     *
     * @param sender  The id of the sender.
     * @param groupId The group id to send the message to.
     * @param content The content of the message.
     * @return The request itself.
     */
    public static ServerRequest groupTextMessageRequest(Id sender, Id groupId,
                                                        TextContent content) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(content);

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.GROUP_MESSAGE, sender);

        builder.addField(Fields.NID, groupId);
        builder.addField(Fields.CONTENT_TYPE, content.getType().getType());
        builder.addField(Fields.TEXT, content.getText());

        return builder.request();
    }

    /**
     * Creates a Group image message request for a Message that has a ImageContent.
     *
     * @param sender  The id of the sender.
     * @param groupId The group id of the recipient.
     * @param content The ImageContent to send.
     * @return The request itself.
     */
    public static ServerRequest groupImageMessageRequest(Id sender, Id groupId,
                                                         ImageContent content) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(content);

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.GROUP_MESSAGE, sender);

        builder.addField(Fields.NID, groupId);
        builder.addField(Fields.CONTENT_TYPE, content.getType().getType());
        builder.addField(Fields.TEXT, content.getCaption());
        builder.addField(Fields.IMAGE, content.getImage());

        return builder.request();
    }

    /**
     * Creates a group history request.
     *
     * @param groupId      The id of the group you want the history from.
     * @param last         The last time we got a message from this group.
     * @param messageCount The max number of message we want.
     * @return The request itself.
     */
    public static ServerRequest groupHistoryRequest(Id senderId, Id groupId, Date last,
                                                    int messageCount) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(last);
        Objects.requireNonNull(messageCount);

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.GROUP_HISTORY, senderId);

        builder.addField(Fields.LAST, last);
        builder.addField(Fields.COUNT, messageCount);
        builder.addField(Fields.NID, groupId);

        return builder.request();
    }

    /**
     * Creates a simple ping request.
     *
     * @param senderId The Id of the sender of this request.
     * @param content  A string that is added to the ping.
     * @return The request itself.
     */
    public static ServerRequest pingRequest(Id senderId, String content) {
        Objects.requireNonNull(senderId);
        Objects.requireNonNull(content);

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.PING, senderId);

        builder.addField(Fields.TEXT, content);

        return builder.request();
    }

    /**
     * Constructor of a RequestBuilder.
     *
     * @param kind   The kind of request to be built.
     * @param sender The sender of the request.
     */
    private RequestBuilder(ServiceRequest.RequestKind kind, Id sender) {
        this.mKind = kind;
        this.mSender = sender;
        this.mConstructingMap = new ArrayMap<>();
    }

    /**
     * Here are the methods allowing us to add fields to the request builder.
     *
     * @param fieldType The type of the field to be added.
     * @param field     The value of this field.
     */
    private void addField(Fields fieldType, String field) {
        this.mConstructingMap.put(fieldType.getValue(), field);
    }

    private void addField(Fields fieldType, List field) {
        this.mConstructingMap.put(fieldType.getValue(), new JSONArray(field));
    }

    private void addField(Fields fieldType, Id field) {
        this.mConstructingMap.put(fieldType.getValue(), Long.parseLong(field.getId()));
    }

    private void addField(Fields fieldType, int field) {
        this.mConstructingMap.put(fieldType.getValue(), field);
    }

    private void addField(Fields fieldType, Date field) {
        this.mConstructingMap.put(fieldType.getValue(),
                formatDate(field));
    }

    private void addField(Fields fieldType, Bitmap field) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        field.compress(Bitmap.CompressFormat.PNG, 0, stream);
        this.mConstructingMap.put(fieldType.getValue(), stream.toString());
    }

    private void addField(Fields fieldType, Group.GroupVisibility field) {
        this.mConstructingMap.put(fieldType.getValue(), field.getValue());
    }

    /**
     * Instantiate the request from the reauest builder.
     *
     * @return The instance of the request.
     */
    private ServerRequest request() {
        Map<String, Object> request = new ArrayMap<>();
        request.put(Fields.KIND.getValue(), mKind.getValue());

        Map<String, Object> metadata = new ArrayMap<>();
        metadata.put("sender", Long.parseLong(mSender.getId()));

        metadata.put("datetime", formatDate(
                new Date()));

        request.put("metadata", new JSONObject(metadata));

        request.put("message", new JSONObject(mConstructingMap));

        return new ServerRequest(new JSONObject(request));
    }

    /**
     * Format a date.
     *
     * @param date The date to be formatted.
     * @return The corresponding formatted format for this date.
     */
    private String formatDate(Date date) {
        Objects.requireNonNull(date);
        return DateSerialization.toString(date);
    }
}
