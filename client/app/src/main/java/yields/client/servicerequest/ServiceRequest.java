package yields.client.servicerequest;


import android.app.Service;

import yields.client.serverconnection.Request;
import yields.client.serverconnection.Response;

public abstract class ServiceRequest{

    /**
     * The Kind of messages possible
     */
    public enum MessageKind {
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

    private static int sNextId = 0;
    private final int rid;

    public ServiceRequest() {
        this.rid = getNextId();
    }

    private static int getNextId() {
        return sNextId++;
    }

    abstract public String getType();

    abstract public Request parseRequestForServer();

    abstract public void serviceActionOnResponse(Service service, Response response);
}








