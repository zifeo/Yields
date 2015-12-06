package yields.client.service;

import yields.client.cache.CacheDatabaseHelper;
import yields.client.serverconnection.ServerRequest;
import yields.client.servicerequest.GroupAddRequest;
import yields.client.servicerequest.GroupCreateRequest;
import yields.client.servicerequest.GroupInfoRequest;
import yields.client.servicerequest.GroupRemoveRequest;
import yields.client.servicerequest.GroupUpdateImageRequest;
import yields.client.servicerequest.GroupUpdateNameRequest;
import yields.client.servicerequest.NodeHistoryRequest;
import yields.client.servicerequest.NodeMessageRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserEntourageAddRequest;
import yields.client.servicerequest.UserEntourageRemoveRequest;
import yields.client.servicerequest.UserGroupListRequest;
import yields.client.servicerequest.UserInfoRequest;
import yields.client.servicerequest.UserSearchRequest;
import yields.client.servicerequest.UserUpdateNameRequest;
import yields.client.yieldsapplication.YieldsApplication;

public class RequestHandler {

    protected final YieldService mService;
    protected final CacheDatabaseHelper mCacheHelper;
    protected final ServiceRequestController mController;

    public RequestHandler(CacheDatabaseHelper cacheDatabaseHelper, YieldService yieldService,
                          ServiceRequestController controller) {
        mCacheHelper = cacheDatabaseHelper;
        mService = yieldService;
        mController = controller;
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserGroupListRequest(UserGroupListRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserEntourageRemoveRequest(UserEntourageRemoveRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserInfoRequest(UserInfoRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupCreateRequest(GroupCreateRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserEntourageAddRequest(UserEntourageAddRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupUpdateImageRequest(GroupUpdateImageRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupUpdateNameRequest(GroupUpdateNameRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupAddRequest(GroupAddRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupRemoveRequest(GroupRemoveRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserUpdateRequest(UserUpdateNameRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserConnectRequest(ServiceRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handlePingRequest(ServiceRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleNodeMessageRequest(NodeMessageRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        YieldsApplication.getUser().modifyGroup(serviceRequest.getReceivingNodeId())
                .setLastUpdate(serviceRequest.getMessage().getDate());

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleNodeHistoryRequest(NodeHistoryRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupInfoRequest(GroupInfoRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    public void handleUserSearchRequest(UserSearchRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }
}
