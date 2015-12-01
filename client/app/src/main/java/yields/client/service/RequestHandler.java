package yields.client.service;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import yields.client.cache.CacheDatabaseHelper;
import yields.client.exceptions.CacheDatabaseException;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.serverconnection.ServerRequest;
import yields.client.servicerequest.GroupAddRequest;
import yields.client.servicerequest.GroupCreateRequest;
import yields.client.servicerequest.GroupHistoryRequest;
import yields.client.servicerequest.GroupInfoRequest;
import yields.client.servicerequest.GroupRemoveRequest;
import yields.client.servicerequest.GroupUpdateImageRequest;
import yields.client.servicerequest.GroupUpdateNameRequest;
import yields.client.servicerequest.GroupUpdateVisibilityRequest;
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
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleUserGroupListRequest(UserGroupListRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            List<Group> groups = mCacheHelper.getAllGroups();
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            Log.d("Y:" + this.getClass().getName(), "Couldn't handle UserGroupListRequest correctly !");
        }

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleUserEntourageRemoveRequest(UserEntourageRemoveRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.updateUser(serviceRequest.getUserToRemove());
        } catch (CacheDatabaseException e) {
            Log.d("Y:" + this.getClass().getName(), "Couldn't handle UserEntourageRemove correctly !");
        }

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleUserInfoRequest(UserInfoRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mCacheHelper.getUser(serviceRequest.getUserInfoId());

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleGroupCreateRequest(GroupCreateRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.addGroup(serviceRequest.getGroup());
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            Log.d("Y:" + this.getClass().getName(), "Couldn't handle GroupCreateRequest correctly !");
        }


        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleUserEntourageAddRequest(UserEntourageAddRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.addUser(YieldsApplication.getUser(serviceRequest.getUserToAdd()));
        } catch (CacheDatabaseException e) {
            Log.d("Y:" + this.getClass().getName(), "Couldn't handle UserEntourageAddRequest correctly !");
        }

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleGroupUpdateVisibilityRequest(GroupUpdateVisibilityRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mCacheHelper.updateGroupVisibility(serviceRequest.getGroupId(), serviceRequest.getNewGroupVisibility());
        //TODO : Notify app

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleGroupUpdateImageRequest(GroupUpdateImageRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mCacheHelper.updateGroupImage(serviceRequest.getGroupId(), serviceRequest.getNewGroupImage());
        //TODO : Notify app

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleGroupUpdateNameRequest(GroupUpdateNameRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mCacheHelper.updateGroupName(serviceRequest.getGroupId(), serviceRequest.getNewGroupName());
        //TODO : Notify app

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleGroupAddRequest(GroupAddRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.addUserToGroup(serviceRequest.getGroupId(), serviceRequest.getUser());
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            Log.d("Y:" + this.getClass().getName(), "Couldn't handle handleGroupAddRequest correctly !");
        }

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleGroupRemoveRequest(GroupRemoveRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mCacheHelper.removeUserFromGroup(serviceRequest.getGroupId(), serviceRequest.getUserToRemoveId());
        //TODO : Notify app

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleUserUpdateRequest(UserUpdateNameRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.updateUser(serviceRequest.getUser());
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            Log.d("Y:" + this.getClass().getName(), "Couldn't handle UserUpdateNameRequest correctly !");
        }

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleUserConnectRequest(ServiceRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handlePingRequest(ServiceRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleNodeMessageRequest(NodeMessageRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        /*try {
            mCacheHelper.addMessage(serviceRequest.getMessage(), serviceRequest.getReceivingNode().getId());
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            Log.d("Y:" + this.getClass().getName(), "Couldn't handle NodeMessageRequest correctly !");
        }*/

        YieldsApplication.getUser().modifyGroup(serviceRequest.getReceivingNode().getId())
                .setLastUpdate(serviceRequest.getMessage().getDate());

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleNodeHistoryRequest(GroupHistoryRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            List<Message> messages = mCacheHelper.getMessagesForGroup(serviceRequest.getGroup(),
                    serviceRequest.getDate(), GroupHistoryRequest.MESSAGE_COUNT);
            mService.receiveMessages(serviceRequest.getGroup().getId(), messages);
        } catch (CacheDatabaseException e) {
            Log.d("Y:" + this.getClass().getName(), "Couldn't handle NodeHistoryRequest correctly !");
        }


        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a group info request
     */
    protected void handleGroupInfoRequest(GroupInfoRequest serviceRequest) {
        mController.sendToServer(serviceRequest.parseRequestForServer());
    }

    public void handleUserSearchRequest(UserSearchRequest serviceRequest) {
        mController.sendToServer(serviceRequest.parseRequestForServer());
    }
}
