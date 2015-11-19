package yields.client.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * The response object which represents a response to be send to the server.
 */
public class Response{
    /**
     * The Kind of messages possible
     */
    public enum ResKind {
        GROUPHISTORYRES("GroupHistoryRes");
        /*PING("PING"), USERCONNECT("UserConnect"), USERUPDATE("UserUpdate"),
        USERGROUPLIST("UserGroupList"), USERENTOURAGEADD("UserEntourageAdd"),
        USERENTOURAGEREMOVE("UserEntourageRemove"), USERSTATUS("UserStatus"),
        GROUPCREATE("GroupCreate"), GROUPUPDATENAME("GroupUpdateName"),
        GROUPUPDATEVISIBILITY("GroupUpdateVisibility"), GROUPUPDATEIMAGE
                ("GroupUpdateImage"),
        GROUPADD("GroupAdd"), GROUPREMOVE("GroupRemove"),
        GROUPMESSAGE("GroupMessage"), GROUPHISTORY("GroupHistory");*/

        private final String name;
        ResKind(String name) { this.name = name; }
        public String getValue() { return name; }
        public static ResKind getEnumbyName(String name){
            for(ResKind e : ResKind.values()){
                if(name.equals(e.name)) return e;
            }
            return null;
        }
    }

    private JSONObject mResponseObj;
    private ResKind mKind;

    /**
     * Creates a response as a Json Object
     *
     * @param rawResponse The String of the response
     * @throws JSONException In case of problems parsing the response
     */
    public Response(String rawResponse) throws JSONException {
        Objects.requireNonNull(rawResponse);
        this.mResponseObj = new JSONObject(rawResponse);
        mKind = ResKind.getEnumbyName(mResponseObj.getString("kind"));
        if(mKind == null) {
            throw new JSONException("mKind is not known");
        }
    }

    /**
     * Gets the Json Object of the response
     *
     * @return The Json Object
     */
    public JSONObject object() {
        return mResponseObj;
    }

    /**
     * Getter for the response in raw format.
     * @return The response in raw format.
     */
    protected String rawResponse(){
        return mResponseObj.toString();
    }

    /**
     * Gets the kind of the response
     *
     * @return The kind
     */
    public ResKind getKind(){
        return mKind;
    }

    /**
     * Gets the Json object of the message from the server response.
     *
     * @return The message from the server.
     * @throws JSONException In case of Json trouble.
     */
    public JSONObject getMessage() throws JSONException{
        return mResponseObj.getJSONObject("message");
    }

    /**
     * Gets the Json object of the metadata from the server response.
     *
     * @return The metadata from the server response.
     * @throws JSONException In case of Json trouble.
     */
    public JSONObject getMetadata() throws JSONException{
        return mResponseObj.getJSONObject("metadata");
    }

}
