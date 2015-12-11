package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * The response object which represents a response received from the server.
 */
public class Response {

    /**
     * The Kind of possible Responses.
     */
    public enum ResKind {

        USER_CONNECT_RESPONSE("UserConnectRes"),
        USER_UPDATE_RESPONSE("UserUpdateRes"), USER_UPDATE_BCAST("UserUpdateBrd"),
        USER_INFO_RESPONSE("UserInfoRes"),
        USER_NODE_LIST_RESPONSE("UserNodeListRes"),
        USER_SEARCH_RESPONSE("UserSearchRes"),

        NODE_HISTORY_RESPONSE("NodeHistoryRes"),
        NODE_SEARCH_RESPONSE("NodeSearchRes"),
        NODE_MESSAGE_BCAST("NodeMessageBrd"),

        GROUP_CREATE_RESPONSE("GroupCreateRes"), GROUP_CREATE_BCAST("GroupCreateBrd"),
        GROUP_UPDATE_RESPONSE("GroupUpdateRes"), GROUP_UPDATE_BCAST("GroupUpdateBrd"),
        GROUP_INFO_RESPONSE("GroupInfoRes"),
        GROUP_MESSAGE_RESPONSE("GroupMessageRes"),

        PUBLISHER_CREATE_RESPONSE("PublisherCreateRes"), PUBLISHER_CREATE_BCAST("PublisherCreateBrd"),
        PUBLISHER_UPDATE_BCAST("PublisherUpdateBrd"),
        PUBLISHER_INFO_RESPONSE("PublisherInfoRes"),
        PUBLISHER_MESSAGE_RESPONSE("PublisherMessageRes"),

        RSS_CREATE_RESPONSE("RSSCreateRes"), RSS_CREATE_BCAST("RSSCreateBrd"),
        RSS_INFO_RES("RSSInfoRes"),

        MEDIA_MESSAGE_RESPONSE("MediaMessageRes");

        private final String name;

        ResKind(String name) {
            this.name = name;
        }

        public String getValue() {
            return name;
        }

        public static ResKind getEnumByName(String name) {
            for (ResKind e : ResKind.values()) {
                if (name.equals(e.name)) {
                    return e;
                }
            }
            return null;
        }
    }

    private final JSONObject mResponseObj;
    private final ResKind mKind;

    /**
     * Creates a response as a Json Objects.
     *
     * @param rawResponse The String of the response.
     * @throws JSONException In case of problems parsing the response.
     */
    public Response(String rawResponse) throws JSONException {
        Objects.requireNonNull(rawResponse);
        this.mResponseObj = new JSONObject(rawResponse);
        mKind = ResKind.getEnumByName(mResponseObj.getString("kind"));
        if (mKind == null) {
            throw new JSONException("mKind is not known " + mResponseObj.getString("kind"));
        }
    }

    /**
     * Gets the Json Object of the response.
     *
     * @return The Json Object.
     */
    public JSONObject object() {
        return mResponseObj;
    }

    /**
     * Getter for the response in raw format.
     *
     * @return The response in raw format.
     */
    protected String rawResponse() {
        return mResponseObj.toString();
    }

    /**
     * Gets the kind of the response.
     *
     * @return The kind.
     */
    public ResKind getKind() {
        return mKind;
    }

    /**
     * Gets the Json object of the message from the server response.
     *
     * @return The message from the server.
     * @throws JSONException In case of Json trouble.
     */
    public JSONObject getMessage() throws JSONException {
        return mResponseObj.getJSONObject("message");
    }

    /**
     * Gets the Json object of the metadata from the server response.
     *
     * @return The metadata from the server response.
     * @throws JSONException In case of Json trouble.
     */
    public JSONObject getMetadata() throws JSONException {
        return mResponseObj.getJSONObject("metadata");
    }

}
