package yields.client.servicerequest;

import yields.client.serverconnection.ServerRequest;

/**
 * Abstract class for Requests to the Service.
 */
public abstract class ServiceRequest {

    /**
     * The Kind of request possible
     */
    public enum RequestKind {

        PING("PING"),

        USER_CONNECT("UserConnect"),
        USER_UPDATE("UserUpdate"), USER_UPDATE_NAME("UserUpdate"),
        USER_ENTOURAGE_ADD("UserEntourageAdd"), USER_ENTOURAGE_REMOVE("UserEntourageRemove"),
        USER_INFO("UserInfo"),
        USER_NODE_LIST("UserNodeList"),
        USER_SEARCH("UserSearch"),

        NODE_HISTORY("NodeHistory"),
        NODE_INFO("NodeInfo"),
        NODE_SEARCH("NodeSearch"),

        GROUP_CREATE("GroupCreate"), GROUP_UPDATE_NODES("GroupUpdate"),
        GROUP_UPDATE("GroupUpdate"), GROUP_UPDATE_NAME("GroupUpdate"),
        GROUP_UPDATE_IMAGE("GroupUpdate"), GROUP_UPDATE_USERS("GroupUpdate"),
        GROUP_UPDATE_TAGS("GroupUpdate"),
        GROUP_INFO("GroupInfo"),
        GROUP_MESSAGE("GroupMessage"),

        PUBLISHER_CREATE("PublisherCreate"),
        PUBLISHER_INFO("PublisherInfo"),
        PUBLISHER_MESSAGE("PublisherMessage"),

        RSS_CREATE("RSSCreate"),

        MEDIA_MESSAGE("MediaMessage");

        private final String mName;

        /**
         * Enumeration constructor
         *
         * @param name The String representation of the request type
         */
        RequestKind(String name) {
            mName = name;
        }

        /**
         * Get's the String representation of the request
         *
         * @return The String representation of the request
         */
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
}








