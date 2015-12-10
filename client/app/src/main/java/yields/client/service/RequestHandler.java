package yields.client.service;

import android.util.Log;

import java.util.List;

import yields.client.activities.NotifiableActivity;
import yields.client.cache.CacheDatabaseHelper;
import yields.client.exceptions.CacheDatabaseException;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.ServerRequest;
import yields.client.servicerequest.GroupCreateRequest;
import yields.client.servicerequest.GroupMessageRequest;
import yields.client.servicerequest.GroupUpdateImageRequest;
import yields.client.servicerequest.GroupUpdateNameRequest;
import yields.client.servicerequest.GroupUpdateTagsRequest;
import yields.client.servicerequest.GroupUpdateUsersRequest;
import yields.client.servicerequest.MediaMessageRequest;
import yields.client.servicerequest.NodeHistoryRequest;
import yields.client.servicerequest.NodeSearchRequest;
import yields.client.servicerequest.RSSCreateRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserEntourageAddRequest;
import yields.client.servicerequest.UserEntourageRemoveRequest;
import yields.client.servicerequest.UserGroupListRequest;
import yields.client.servicerequest.UserInfoRequest;
import yields.client.servicerequest.UserSearchRequest;
import yields.client.servicerequest.UserUpdateNameRequest;
import yields.client.servicerequest.UserUpdateRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Handles ServiceRequests from the app.
 * It sends the appropriate requests to the server and updates the cache.
 */
public class RequestHandler {

    protected final YieldService mService;
    protected final CacheDatabaseHelper mCacheHelper;
    protected final ServiceRequestController mController;

    /**
     * Main constructor for a RequestHandler.
     *
     * @param cacheDatabaseHelper The helper for the associated cache.
     * @param yieldService        The service using the handler.
     * @param controller          The controller of the service using this handler.
     */
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
        List<Group> groups = mCacheHelper.getAllGroups();
        if (!groups.isEmpty()) {
            for (Group group : groups) {
                YieldsApplication.getUser().addGroup(group);
            }
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        }

        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserEntourageRemoveRequest(UserEntourageRemoveRequest serviceRequest) {
        mCacheHelper.updateEntourage(serviceRequest.getUserToRemove(), false);

        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserInfoRequest(UserInfoRequest serviceRequest) {
        User userFromCache = mCacheHelper.getUser(serviceRequest.getUserInfoId());
        if (userFromCache != null) {
            User userInApp = YieldsApplication.getUserFromId(userFromCache.getId());

            List<User> entourage = mCacheHelper.getClientUserEntourage();
            boolean inEntourageOrClientUser =
                    entourage.contains(userFromCache) || userFromCache.equals(YieldsApplication.getUser());

            if (!inEntourageOrClientUser) {
                if (userInApp != null) {
                    userInApp.update(userFromCache);
                } else {
                    YieldsApplication.addNotKnown(userFromCache);
                }
            } else {
                if (YieldsApplication.getUser().equals(userFromCache)) {
                    YieldsApplication.getUser().update(userFromCache);
                } else {
                    if (userInApp == null) {
                        YieldsApplication.getUser().addUserToEntourage(userFromCache);
                    } else {
                        userInApp.update(userFromCache);
                    }
                }
            }
        }

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
    protected void handleRssCreateRequest(RSSCreateRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserEntourageAddRequest(UserEntourageAddRequest serviceRequest) {
        mCacheHelper.updateEntourage(serviceRequest.getUserToAdd(), true);

        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupUpdateImageRequest(GroupUpdateImageRequest serviceRequest) {
        mCacheHelper.updateGroupImage(serviceRequest.getGroupId(), serviceRequest.getNewGroupImage());

        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupUpdateNameRequest(GroupUpdateNameRequest serviceRequest) {
        mCacheHelper.updateGroupName(serviceRequest.getGroupId(), serviceRequest.getNewGroupName());

        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupUpdateUsersRequest(GroupUpdateUsersRequest serviceRequest) {
        if (serviceRequest.getUpdateType().equals(GroupUpdateUsersRequest.UpdateType.ADD)) {
            mCacheHelper.addUsersToGroup(serviceRequest.getGroupId(), serviceRequest.getUsersToUpdate());
        } else {
            mCacheHelper.removeUsersFromGroup(serviceRequest.getGroupId(), serviceRequest.getUsersToUpdate());
        }

        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupUpdateNodesRequest(GroupUpdateTagsRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }


    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserUpdateRequest(UserUpdateRequest serviceRequest) {
        mCacheHelper.addUser(serviceRequest.getUser());

        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    protected void handleUserUpdateNameRequest(UserUpdateNameRequest serviceRequest) {
        mCacheHelper.updateUserName(serviceRequest.getUser().getId(), serviceRequest.getUser().getName());

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
    protected void handleNodeMessageRequest(GroupMessageRequest serviceRequest) {
        mCacheHelper.addMessage(serviceRequest.getMessage(), serviceRequest.getReceivingNodeId());
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();

        YieldsApplication.getUser().getGroup(serviceRequest.getReceivingNodeId())
                .setLastUpdate(serviceRequest.getMessage().getDate());

        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleNodeHistoryRequest(NodeHistoryRequest serviceRequest) {
        try {
            List<Message> messages = mCacheHelper.getMessagesForGroup(serviceRequest.getGroup(),
                    serviceRequest.getDate(), NodeHistoryRequest.MESSAGE_COUNT);
            mService.receiveMessages(serviceRequest.getGroup(), messages);
            Log.d("Y:" + this.getClass().toString(), "Received " + messages.size() + " from cache !");
        } catch (CacheDatabaseException e) {
            Log.d("Y:" + this.getClass().toString(), "Failed to retrieve messages from Cache !");
        }

        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleGroupInfoRequest(ServiceRequest serviceRequest) {
        //TODO : see with Trofleb
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleUserSearchRequest(UserSearchRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleNodeSearchRequest(NodeSearchRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }

    /**
     * Handles the appropriate ServiceRequest which is given to it by argument.
     */
    protected void handleMediaMessageRequest(MediaMessageRequest serviceRequest) {
        mCacheHelper.addMessage(serviceRequest.getMessage(), serviceRequest.getReceivingNodeId());


        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mController.sendToServer(serverRequest);
    }
}
