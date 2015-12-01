package yields.client.serverconnection;

import android.graphics.Bitmap;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import yields.client.exceptions.ContentException;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserUpdateRequest;

/**
 * A builder for requests that will be send to the server
 */
public class RequestBuilder {

    /**
     * The Fields possible for the request
     */
    public enum Fields {
        EMAIL("email"), TEXT("text"), NAME("name"),
        NODES("nodes"), KIND("kind"), USERS("users"),
        LAST("datetime"), TO("to"), CONTENT("content"), COUNT("count"),
        IMAGE("pic"), NID("nid"), VISIBILITY("visibility"),
        CONTENT_TYPE("contentType"), UID("uid"),
        TAG("tags"), DATE("date"), ADD_ENTOURAGE("addEntourage"),
        REMOVE_ENTOURAGE("removeEntourage"), PATTERN("pattern");

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
     * @param sender The sender of the request, which wants to be updated.
     * @param name   The name of the updated User.
     * @param email  The email ot the updated User.
     * @param image  The image of the updated User.
     * @return The appropriate ServerRequest.
     */
    private static ServerRequest userUpdateRequest(Id sender, String name, String email,
                                                   Bitmap image, List<Long> addEntourage,
                                                   List<Long> remEntourage) {
        Objects.requireNonNull(sender);

        RequestBuilder builder = new RequestBuilder(ServiceRequest.RequestKind.USER_UPDATE, sender);

        builder.addOptionalField(Fields.NAME, name);
        builder.addOptionalField(Fields.EMAIL, email);
        builder.addOptionalField(Fields.IMAGE, image);
        builder.addOptionalField(Fields.ADD_ENTOURAGE, addEntourage);
        builder.addOptionalField(Fields.REMOVE_ENTOURAGE, remEntourage);

        return builder.request();
    }

    /**
     * ServerRequest for updating user properties.
     *
     * @param sender The sender of the request, which wants to be updated.
     * @param name   The name of the updated User.
     * @param email  The email ot the updated User.
     * @param image  The image of the updated User.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest userUpdateRequest(Id sender, String name, String email,
                                                  Bitmap image) {
        return userUpdateRequest(sender, name, email, image, new ArrayList<Long>(),
                new ArrayList<Long>());
    }

    /**
     * ServerRequest for updating user properties.
     *
     * @param sender The sender of the request, which wants to be updated.
     * @param name   The name of the updated User.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest userUpdateRequest(Id sender, String name) {
        return userUpdateRequest(sender, name, null, null, new ArrayList<Long>(), new ArrayList<Long>());
    }

    /**
     * ServerRequest for retrieving information on a Group.
     *
     * @param sender  The Id of the sender of the request, which wants the information.
     * @param groupId The Id of the Group.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest groupInfoRequest(Id sender, Id groupId) {
        Objects.requireNonNull(sender);

        RequestBuilder builder = new RequestBuilder(ServiceRequest.RequestKind.GROUP_INFO, sender);

        builder.addField(Fields.NID, groupId);

        return builder.request();
    }

    /**
     * ServerRequest to receive the group list.
     *
     * @param senderId The senderId of the request.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest userGroupListRequest(Id senderId) {
        Objects.requireNonNull(senderId);
        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.USER_GROUP_LIST, senderId);

        builder.addField(Fields.UID, senderId);

        return builder.request();
    }

    /**
     * ServerRequest for adding a 'contact' to the user entourage list.
     *
     * @param sender The sender of the request.
     * @param userAdded  Id of the new contact to add.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest userEntourageAddRequest(Id sender, Id userAdded) {
        List<Long> userAdd = new ArrayList<Long>();
        userAdd.add(userAdded.getId());

        return userUpdateRequest(sender, null, null,
                null, userAdd, new ArrayList<Long>());
    }

    /**
     * ServerRequest for removing a 'contact' from the user entourage list.
     *
     * @param sender The sender of the request.
     * @param email  Email of the contact to remove.
     * @return The appropriate ServerRequest.
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
     * @return The appropriate ServerRequest.
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
     * ServerRequest for searching a User by his email.
     *
     * @param sender The sender of the request.
     * @param email  The email of the user being searched for.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest userSearchRequest(Id sender, String email) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(email);
        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.USER_SEARCH, sender);

        builder.addField(Fields.EMAIL, email);

        return builder.request();
    }

    /**
     * Created a User information request.
     *
     * @param sender   The Id of the sender of the request.
     * @param userInfo The Id of the User from which information shall be retrieved.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest userInfoRequest(Id sender, Id userInfo) {
        Objects.requireNonNull(sender);

        RequestBuilder builder = new RequestBuilder(ServiceRequest.RequestKind.USER_INFO, sender);
        builder.addField(Fields.UID, userInfo);

        return builder.request();
    }

    /**
     * Creates a Group create request.
     *
     * @param sender     The id of the sender.
     * @param name       The new name of the group.
     * @param visibility The visibility of the group.
     * @param users      The users attached to the group.
     * @return The request itself.
     */
    public static ServerRequest groupCreateRequest(Id sender, String name,
                                                   Group.GroupVisibility visibility,
                                                   List<Id> users) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(name);
        Objects.requireNonNull(users);

        if (users.size() < 1) {
            throw new IllegalArgumentException("No nodes to add...");
        }
        List<Long> usersId = new ArrayList<>();
        for (Id id : users) {
            usersId.add(id.getId());
        }

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.GROUP_CREATE, sender);

        builder.addField(Fields.NAME, name);
        // TODO : correct when nodes are really implemented
        builder.addField(Fields.NODES, new ArrayList());
        builder.addField(Fields.USERS, usersId);
        builder.addField(Fields.VISIBILITY, visibility);
        builder.addField(Fields.TAG, new ArrayList());

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

        RequestBuilder builder = new RequestBuilder(ServiceRequest.RequestKind.GROUP_UPDATE, sender);
        builder.addField(Fields.NID, groupId);
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
                .GROUP_UPDATE, sender);
        builder.addField(Fields.NID, groupId);
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
                .GROUP_UPDATE, sender);
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
        builder.addField(Fields.UID, newUser);

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
     * Creates a Node message request for a Message (no matter what it's Content is).
     *
     * @param sender  The Id of the sender.
     * @param groupId The Id of the group to which the Message is sent to.
     * @param content The Content of the Message that is sent.
     * @param date    The date of when the Message was created.
     * @return The request itself.
     */
    public static ServerRequest nodeMessageRequest(Id sender, Id groupId, Content content, Date date) {
        switch (content.getType()) {
            case TEXT:
                return nodeTextMessageRequest(sender, groupId, (TextContent) content, date);
            case IMAGE:
                return nodeImageMessageRequest(sender, groupId, (ImageContent) content, date);
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
    private static ServerRequest nodeTextMessageRequest(Id sender, Id groupId,
                                                        TextContent content, Date date) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(content);
        Objects.requireNonNull(date);

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.NODE_MESSAGE, sender);

        builder.addField(Fields.NID, groupId);
        builder.addOptionalField(Fields.CONTENT_TYPE, null);
        builder.addField(Fields.TEXT, content.getText());
        builder.addField(Fields.DATE, DateSerialization.dateSerializer.toString(date));
        builder.addOptionalField(Fields.CONTENT, null);


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
    private static ServerRequest nodeImageMessageRequest(Id sender, Id groupId,
                                                         ImageContent content, Date date) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(content);
        Objects.requireNonNull(date);

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.NODE_MESSAGE, sender);

        builder.addField(Fields.NID, groupId);
        builder.addField(Fields.CONTENT_TYPE, content.getType().toString().toLowerCase());
        builder.addField(Fields.TEXT, content.getCaption());
        builder.addField(Fields.CONTENT, content.getImage());
        builder.addField(Fields.DATE, DateSerialization.dateSerializer.toString(date));

        return builder.request();
    }

    /**
     * Creates a node history request.
     *
     * @param groupId      The id of the group you want the history from.
     * @param last         The last time we got a message from this group.
     * @param messageCount The max number of message we want.
     * @return The request itself.
     */
    public static ServerRequest nodeHistoryRequest(Id senderId, Id groupId, Date last,
                                                   int messageCount) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(last);
        Objects.requireNonNull(messageCount);

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.NODE_HISTORY, senderId);

        builder.addField(Fields.LAST, last);
        builder.addField(Fields.COUNT, messageCount);
        builder.addField(Fields.NID, groupId);

        return builder.request();
    }

    /**
     * Creates a node search request.
     *
     * @param senderId
     * @param pattern
     * @return
     */
    public static ServerRequest nodeSearchRequest(Id senderId, String pattern) {
        Objects.requireNonNull(senderId);
        Objects.requireNonNull(pattern);

        RequestBuilder builder = new RequestBuilder(
                ServiceRequest.RequestKind.NODE_SEARCH, senderId);

        builder.addField(Fields.PATTERN, pattern);

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

    private <T> Boolean addOptionalField(Fields fieldType, T field) {
        if (field == null) {
            this.mConstructingMap.put(fieldType.getValue(), JSONObject.NULL);
        } else {
            addField(fieldType, field);
        }

        return true;
    }

    /**
     * Here are the methods allowing us to add fields to the request builder.
     *
     * @param fieldType The type of the field to be added.
     * @param field     The value of this field.
     */
    private void addField(Fields fieldType, Object field) {
        this.mConstructingMap.put(fieldType.getValue(), field);
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
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        int width = field.getWidth();
        int height = field.getHeight();
        double ratio = width > height ? 800.0/width : 800.0/height;



        Bitmap.createScaledBitmap(field, (int) (width*ratio), (int) (height*ratio), true)
                .compress(Bitmap.CompressFormat.JPEG, 20, stream);

        this.mConstructingMap.put(fieldType.getValue(),
                Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT));
    }

    private void addField(Fields fieldType, Group.GroupVisibility field) {
        this.mConstructingMap.put(fieldType.getValue(), field.getValue().toLowerCase());
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
        metadata.put("client", mSender.getId());

        Date ref = new Date();
        try {
            ref = mKind.equals(ServiceRequest.RequestKind.NODE_MESSAGE) ?
                    DateSerialization.dateSerializer.toDate((String) mConstructingMap.get(Fields.DATE.getValue()))
                    : new Date();
        } catch (ParseException e) {
            Log.d("RequestBuilder", "Couldn't handle build ServerRequest correctly !");
        }

        metadata.put("datetime", formatDate(new Date()));

        metadata.put("ref", formatDate(ref));

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
        return DateSerialization.dateSerializer.toString(date);
    }
}
