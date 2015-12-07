package yields.client.serverconnection;

import android.graphics.Bitmap;
import android.util.ArrayMap;
import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import yields.client.exceptions.ContentException;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
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
        EMAIL("email"), TEXT("text"), NAME("name"),
        NODES("nodes"), KIND("kind"), USERS("users"),
        LAST("datetime"), TO("to"), CONTENT("content"), COUNT("count"),
        IMAGE("pic"), NID("nid"), VISIBILITY("visibility"),
        CONTENT_TYPE("contentType"), UID("uid"),
        TAG("tags"), DATE("date"), ADD_ENTOURAGE("addEntourage"),
        REMOVE_ENTOURAGE("removeEntourage"), PATTERN("pattern"), ADD_USERS("addUsers"),
        REM_USERS("removeUsers"), ADD_NODES("addNodes"), REM_NODES("removeNodes");

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
                                                   Bitmap image, List<Id> addEntourage,
                                                   List<Id> remEntourage) {
        Objects.requireNonNull(sender);

        RequestBuilder builder = new RequestBuilder(ServiceRequest.RequestKind.USER_UPDATE, sender);

        if (!builder.addNullIfNullField(Fields.NAME, name)) {
            builder.addField(Fields.NAME, name);
        }
        if (!builder.addNullIfNullField(Fields.EMAIL, email)) {
            builder.addField(Fields.EMAIL, email);
        }
        if (!builder.addNullIfNullField(Fields.IMAGE, image)) {
            builder.addField(Fields.IMAGE, image);
        }
        if (!builder.addNullIfNullField(Fields.ADD_ENTOURAGE, addEntourage)) {
            builder.addField(Fields.ADD_ENTOURAGE, addEntourage);
        }
        if (!builder.addNullIfNullField(Fields.REMOVE_ENTOURAGE, remEntourage)) {
            builder.addField(Fields.REMOVE_ENTOURAGE, remEntourage);
        }

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
    public static ServerRequest userUpdateInfoRequest(Id sender, String name, String email,
                                                      Bitmap image) {
        return userUpdateRequest(sender, name, email, image, null,
                null);
    }

    /**
     * ServerRequest for updating user properties.
     *
     * @param sender The sender of the request, which wants to be updated.
     * @param name   The name of the updated User.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest userUpdateNameRequest(Id sender, String name) {
        return userUpdateRequest(sender, name, null, null, null, null);
    }

    /**
     * ServerRequest for adding a 'contact' to the user entourage list.
     *
     * @param sender    The sender of the request.
     * @param userAdded Id of the new contact to add.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest userEntourageAddRequest(Id sender, Id userAdded) {
        Objects.requireNonNull(userAdded);
        List<Id> userAdd = new ArrayList<>();
        userAdd.add(userAdded);

        return userUpdateRequest(sender, null, null,
                null, userAdd, null);
    }

    /**
     * ServerRequest for removing a 'contact' from the user entourage list.
     *
     * @param sender     The sender of the request.
     * @param userRemove Id of the contact to remove.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest userEntourageRemoveRequest(Id sender, Id userRemove) {
        Objects.requireNonNull(userRemove);
        List<Id> userRem = new ArrayList<>();
        userRem.add(userRemove);

        return userUpdateRequest(sender, null, null,
                null, null, userRem);
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
     * @param sender The Id of the sender of the request.
     * @param userId The Id of the User from which information shall be retrieved.
     * @return The appropriate ServerRequest.
     */
    public static ServerRequest userInfoRequest(Id sender, Id userId) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(userId);

        RequestBuilder builder = new RequestBuilder(ServiceRequest.RequestKind.USER_INFO, sender);

        builder.addField(Fields.UID, userId);

        return builder.request();
    }


    //TODO : See with server why no public/private

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
                                                   List<Id> users, List<Id> nodes) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(name);
        Objects.requireNonNull(users);
        Objects.requireNonNull(visibility);

        if (users.size() < 1) {
            throw new IllegalArgumentException("No nodes to add...");
        }

        RequestBuilder builder;

        if (visibility == Group.GroupVisibility.PRIVATE) {
            builder = new RequestBuilder(ServiceRequest.RequestKind.GROUP_CREATE, sender);
        } else {
            builder = new RequestBuilder(ServiceRequest.RequestKind.PUBLISHER_CREATE, sender);
        }

        builder.addField(Fields.NAME, name);
        builder.addField(Fields.NODES, nodes);
        builder.addField(Fields.USERS, users);
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
    private static ServerRequest groupUpdateRequest(Id sender, Id groupId, String newName,
                                                    Bitmap image, List<Id> addusers,
                                                    List<Id> remUsers, List<Id> addNodes,
                                                    List<Id> remNodes) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);

        RequestBuilder builder = new RequestBuilder(ServiceRequest.RequestKind.GROUP_UPDATE, sender);

        builder.addField(Fields.NID, groupId);
        if (!builder.addNullIfNullField(Fields.NAME, newName)) {
            builder.addField(Fields.NAME, newName);
        }
        if (!builder.addNullIfNullField(Fields.IMAGE, image)) {
            builder.addField(Fields.IMAGE, image);
        }
        if (!builder.addNullIfNullField(Fields.ADD_USERS, addusers)) {
            builder.addField(Fields.ADD_USERS, addusers);
        }
        if (!builder.addNullIfNullField(Fields.REM_USERS, remUsers)) {
            builder.addField(Fields.REM_USERS, remUsers);
        }
        if (!builder.addNullIfNullField(Fields.ADD_NODES, addNodes)) {
            builder.addField(Fields.ADD_NODES, addNodes);
        }
        if (!builder.addNullIfNullField(Fields.REM_NODES, remNodes)) {
            builder.addField(Fields.REM_NODES, remNodes);
        }

        return builder.request();
    }

    public static ServerRequest groupUpdateNameRequest(Id senderId, Id groupId, String name) {
        Objects.requireNonNull(name);

        return groupUpdateRequest(senderId, groupId, name, null, null, null, null, null);
    }

    /**
     * ServerRequest for updating the group image.
     *
     * @param senderId Sender of the request.
     * @param groupId  Id of the group having its image changed.
     * @param newImage The new Image
     * @return The request.
     */
    public static ServerRequest groupUpdateImageRequest(Id senderId, Id groupId,
                                                        Bitmap newImage) {
        Objects.requireNonNull(newImage);

        return groupUpdateRequest(senderId, groupId, null, newImage, null, null, null, null);
    }


    /**
     * ServerRequest for adding a new user to a group.
     *
     * @param senderId The sender of the request.
     * @param groupId  Id of the group.
     * @param newUser  The user to add in this group.
     * @return The request.
     */
    public static ServerRequest groupAddRequest(Id senderId, Id groupId,
                                                Id newUser) {
        Objects.requireNonNull(senderId);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newUser);

        ArrayList<Id> addUsers = new ArrayList<>();

        addUsers.add(newUser);

        return groupUpdateRequest(senderId, groupId, null, null, addUsers, null, null, null);
    }

    /**
     * ServerRequest for adding a new user to a group.
     *
     * @param senderId The sender of the request.
     * @param groupId  Id of the group.
     * @param newUser  The user to add in this group.
     * @return The request.
     */
    public static ServerRequest groupAddRequest(Id senderId, Id groupId,
                                                List<Id> newUser) {
        Objects.requireNonNull(senderId);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(newUser);

        return groupUpdateRequest(senderId, groupId, null, null, newUser, null, null, null);
    }

    /**
     * ServerRequest for removing a user from a group.
     *
     * @param senderId     The sender of the request.
     * @param groupId      Id of the group.
     * @param userToRemove The user to remove from  this group.
     * @return The request.
     */
    public static ServerRequest groupRemoveRequest(Id senderId, Id groupId,
                                                   Id userToRemove) {
        Objects.requireNonNull(senderId);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(userToRemove);

        ArrayList<Id> remUsers = new ArrayList<>();

        remUsers.add(userToRemove);

        return groupUpdateRequest(senderId, groupId, null, null, null, remUsers, null, null);
    }

    /**
     * ServerRequest for removing a user from a group.
     *
     * @param senderId     The sender of the request.
     * @param groupId      Id of the group.
     * @param userToRemove The user to remove from  this group.
     * @return The request.
     */
    public static ServerRequest groupRemoveRequest(Id senderId, Id groupId,
                                                   List<Id> userToRemove) {
        Objects.requireNonNull(senderId);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(userToRemove);

        return groupUpdateRequest(senderId, groupId, null, null, null, userToRemove, null, null);
    }

    /**
     * Builds a message request for the server.
     *
     * @param senderId The sender Id.
     * @param groupId  The group to send to.
     * @param content  The content of the message
     * @param date     The reference date for the message (Id)
     * @return The request for the server
     */
    public static ServerRequest nodeMessageRequest(Id senderId, Id groupId,
                                                   Group.GroupVisibility visibility,
                                                   Content content, Date date) {

        Objects.requireNonNull(senderId);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(date);

        RequestBuilder builder;

        if (visibility.equals(Group.GroupVisibility.PRIVATE)) {
            builder = new RequestBuilder(ServiceRequest.RequestKind.GROUP_MESSAGE, senderId);
        } else {
            builder = new RequestBuilder(ServiceRequest.RequestKind.PUBLISHER_MESSAGE, senderId);
        }

        builder.addField(Fields.NID, groupId);
        builder.addField(Fields.DATE, date);
        builder.addField(Fields.TEXT, content.getTextForRequest());

        switch (content.getType()) {
            case TEXT:
                builder.addNullField(Fields.CONTENT_TYPE);
                builder.addNullField(Fields.CONTENT);
                break;
            case IMAGE:
                builder.addField(Fields.CONTENT_TYPE, "image");
                builder.addField(Fields.CONTENT, content.getContentForRequest());
                break;

            case URL:
                builder.addField(Fields.CONTENT_TYPE, "url");
                builder.addField(Fields.CONTENT, content.getContentForRequest());
                break;

            default:
                throw new ContentException("No such ContentType exists ! " + content.getType().toString());
        }

        return builder.request();
    }

    /**
     * Creates a node history request.
     *
     * @param senderId     The id of the sender.
     * @param groupId      The id of the group you want the history from.
     * @param last         The last time we got a message from this group.
     * @param messageCount The max number of message we want.
     * @return The request itself.
     */
    public static ServerRequest nodeHistoryRequest(Id senderId, Id groupId, Date last,
                                                   int messageCount) {
        Objects.requireNonNull(senderId);
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
     * @param senderId The sender Id.
     * @param pattern  The pattern to search for.
     * @return The nodeSearch request.
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
     *
     */
    private void addNullField(Fields fieldType) {
        this.mConstructingMap.put(fieldType.getValue(), JSONObject.NULL);
    }

    /**
     * Adds a field and initialise it to null if it is null.
     *
     * @param fieldType The field type.
     * @param field     The field to add
     * @return true if the field was null
     */
    private boolean addNullIfNullField(Fields fieldType, Object field) {
        if (field == null) {
            this.mConstructingMap.put(fieldType.getValue(), JSONObject.NULL);
            return true;
        }

        return false;
    }

    /**
     * Adds a field and initialise it to empty list if it is null.
     *
     * @param fieldType The field type.
     * @param field     The field to add
     * @return true if the field was null
     */
    private <T> boolean addNullIfNullField(Fields fieldType, List<T> field) {
        if (field == null) {
            this.mConstructingMap.put(fieldType.getValue(), new ArrayList<>());
            return true;
        }

        return false;
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

    private void addField(Fields fieldType, List<Id> field) {
        List<Long> longList = new ArrayList<>();

        for (Id id : field) {
            longList.add(id.getId());
        }

        this.mConstructingMap.put(fieldType.getValue(), longList);
    }

    private void addField(Fields fieldType, Id field) {
        this.mConstructingMap.put(fieldType.getValue(), field.getId());
    }

    private void addField(Fields fieldType, Date field) {
        this.mConstructingMap.put(fieldType.getValue(),
                formatDate(field));
    }

    private void addField(Fields fieldType, Group.GroupVisibility field) {
        this.mConstructingMap.put(fieldType.getValue(), field.getValue().toLowerCase());
    }

    private void addField(Fields fieldType, Bitmap field) {
        this.mConstructingMap.put(fieldType.getValue(),
                ImageSerialization.serializeImage(field, ImageSerialization.SIZE_IMAGE_NODE));
    }

    /**
     * Instantiate the request from the request builder.
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
            Object date = mConstructingMap.get(Fields.DATE.getValue());
            if (date != null) {
                ref = DateSerialization.dateSerializer.toDate((String) mConstructingMap.get(Fields.DATE.getValue()));
            } else {
                ref = new Date();
            }
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
