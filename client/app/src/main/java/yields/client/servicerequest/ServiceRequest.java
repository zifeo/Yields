package yields.client.servicerequest;


import android.app.Service;

import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * Abstract class for Requests to the Service.
 */
public abstract class ServiceRequest {

    /**
     * The Kind of messages possible
     */
    public enum RequestKind {
        PING("PING"), USERCONNECT("UserConnect"), USERUPDATE("UserUpdateRequest"),
        USERGROUPLIST("UserGroupList"), USERENTOURAGEADD("UserEntourageAdd"),
        USERENTOURAGEREMOVE("UserEntourageRemove"), USERSTATUS("UserStatus"),
        GROUPCREATE("GroupCreate"), GROUPUPDATENAME("GroupUpdateName"),
        GROUPUPDATEVISIBILITY("GroupUpdateVisibility"), GROUPUPDATEIMAGE
                ("GroupUpdateImage"),
        GROUPADD("GroupAdd"), GROUPREMOVE("GroupRemove"),
        GROUPMESSAGE("GroupMessage"), GROUPHISTORY("GroupHistory");

        private final String mName;

        RequestKind(String name) {
            mName = name;
        }

        public String getValue() {
            return mName;
        }
    }

    private static int sNextId = 0;
    private final int rid;

    /**
     * Main abstract constructor for a ServiceRequest.
     */
    public ServiceRequest() {
        this.rid = getNextId();
    }

    private static int getNextId() {
        return sNextId++;
    }

    /**
     * Returns the type of this ServiceRequest as a String.
     *
     * @return The type of this ServiceRequest as a String.
     */
    abstract public RequestKind getType();

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    abstract public ServerRequest parseRequestForServer();

    abstract public void serviceActionOnResponse(Service service, Response response);
}








