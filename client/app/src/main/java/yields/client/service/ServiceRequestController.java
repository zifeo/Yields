package yields.client.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import yields.client.cache.CacheDatabaseHelper;
import yields.client.exceptions.CacheDatabaseException;
import yields.client.exceptions.ServiceRequestException;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.CommunicationChannel;
import yields.client.serverconnection.ConnectionManager;
import yields.client.serverconnection.ConnectionSubscriber;
import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;
import yields.client.serverconnection.YieldsSocketProvider;
import yields.client.servicerequest.GroupAddRequest;
import yields.client.servicerequest.GroupCreateRequest;
import yields.client.servicerequest.GroupHistoryRequest;
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
import yields.client.servicerequest.UserUpdateRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Controller for ServiceRequests.
 */
public class ServiceRequestController {

    private final CacheDatabaseHelper mCacheHelper;
    private final YieldService mService;
    private final AtomicBoolean isConnecting;
    // Not final because we may have to recreate it in case of connection error
    private CommunicationChannel mCommunicationChannel;
    private Thread mConnector;

    public ServiceRequestController(CacheDatabaseHelper cacheDatabaseHelper, YieldService service) {
        mCacheHelper = cacheDatabaseHelper;
        mService = service;
        isConnecting = new AtomicBoolean(true);
        connectToServer();
    }

    /**
     * Handles any error while connecting to the server
     *
     * @param e the exception that was triggered the connection error
     */

    public void handleConnectionError(final IOException e) {
        if (!isConnecting.getAndSet(true)) {
            mService.onServerDisconnected();
            mService.receiveError("Problem connecting to server : " + e.getMessage());
            mConnector = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("Y:" + this.getClass().getName(), "waiting for connection");
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        Log.d("Y:" + this.getClass().getName(),
                                "Interrupted while waiting we therefore try to connect again now.");
                    } finally {
                        connectToServer();
                    }
                }
            });
            mConnector.start();
        }
    }

    /**
     * Notify connector to reconnect faster
     */
    public void notifyConnector() {
        if (mConnector != null && mConnector.isAlive()) {
            mConnector.interrupt();
        }
    }

    /**
     * Test if the server is connected
     *
     * @return
     */

    public boolean isConnected() {
        return !isConnecting.get();
    }

    /**
     * Handles any given ServiceRequest.
     *
     * @param serviceRequest
     */
    public void handleServiceRequest(ServiceRequest serviceRequest) {
        switch (serviceRequest.getType()) {
            case PING:
                handlePingRequest(serviceRequest);
                break;
            case USER_CONNECT:
                handleUserConnectRequest(serviceRequest);
                break;
            case USER_UPDATE:
                handleUserUpdateRequest((UserUpdateRequest) serviceRequest);
                break;
            case USER_GROUP_LIST:
                handleUserGroupListRequest((UserGroupListRequest) serviceRequest);
                break;
            case USER_ENTOURAGE_ADD:
                handleUserEntourageAddRequest((UserEntourageAddRequest) serviceRequest);
                break;
            case USER_ENTOURAGE_REMOVE:
                handleUserEntourageRemoveRequest((UserEntourageRemoveRequest) serviceRequest);
                break;
            case USER_INFO:
                handleUserInfoRequest((UserInfoRequest) serviceRequest);
                break;
            case GROUP_CREATE:
                handleGroupCreateRequest((GroupCreateRequest) serviceRequest);
                break;
            case GROUP_UPDATE_NAME:
                handleGroupUpdateNameRequest((GroupUpdateNameRequest) serviceRequest);
                break;
            case GROUP_UPDATE_VISIBILITY:
                handleGroupUpdateVisibilityRequest((GroupUpdateVisibilityRequest) serviceRequest);
                break;
            case GROUP_UPDATE_IMAGE:
                handleGroupUpdateImageRequest((GroupUpdateImageRequest) serviceRequest);
                break;
            case GROUP_ADD:
                handleGroupAddRequest((GroupAddRequest) serviceRequest);
                break;
            case GROUP_REMOVE:
                handleGroupRemoveRequest((GroupRemoveRequest) serviceRequest);
                break;
            case NODE_MESSAGE:
                handleNodeMessageRequest((NodeMessageRequest) serviceRequest);
                break;
            case NODE_HISTORY:
                handleNodeHistoryRequest((GroupHistoryRequest) serviceRequest);
                break;
            default:
                throw new ServiceRequestException("No such ServiceRequest type !");
        }
    }

    public void handleServerResponse(Response serverResponse) {
        switch (serverResponse.getKind()) {
            case NODE_HISTORY_RESPONSE:
                handleGroupHistoryResponse(serverResponse);
                break;
            case NODE_MESSAGE_RESPONSE:
                handleGroupMessageResponse(serverResponse);
                break;
            case USER_CONNECT_RESPONSE:
                handleUserConnectResponse(serverResponse);
                break;
            case USER_GROUP_LIST_RESPONSE:
                handleUserGroupListResponse(serverResponse);
                break;
            case USER_INFO_RESPONSE:
                handleUserInfoResponse(serverResponse);
                break;
            case GROUP_CREATE_RESPONSE:
                handleGroupCreateResponse(serverResponse);
                break;
            default:
                throw new ServiceRequestException("No such ServiceResponse type !");
                //TODO: In need of another exception ?
        }
    }

    private void handleGroupCreateResponse(Response serverResponse) {
        try {
            YieldsApplication.getUser().activateGroup(
                    new Id(serverResponse.getMessage().getLong("nid")));
            mService.notifyChange();
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleUserInfoResponse(Response serverResponse) {
        try {
            if (YieldsApplication.getUser().getId().getId().equals(-1)){
                YieldsApplication.getUser().update(serverResponse.getMessage());
                JSONArray array = serverResponse.getMessage().getJSONArray("entourage");

                for (int i = 0; i < array.length(); i++) {
                    ServiceRequest userInfoRequest = new UserInfoRequest(YieldsApplication.getUser(),
                            new Id(array.getLong(i)));
                    mService.sendRequest(userInfoRequest);
                }
            } else {
                // TODO: uncomment when server improve response
                /*
                YieldsApplication.getUser()
                    .addUserToEntourage(new User(serverResponse.getMessage()));
                */
            }

        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleUserGroupListResponse(Response serverResponse) {
        try {
            JSONArray groupSeq = serverResponse.getMessage().getJSONArray("groups");
            for (int i = 0; i < groupSeq.length(); i++) {
                JSONArray jsonGroup = groupSeq.getJSONArray(i);
                Group group = new Group(jsonGroup);
                YieldsApplication.getUser().addGroup(group);
                ServiceRequest historyRequest = new GroupHistoryRequest(group, new Date());
                mService.sendRequest(historyRequest);
            }
            mService.notifyChange();
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }

    }

    private void handleUserConnectResponse(Response serverResponse) {
        ClientUser user = YieldsApplication.getUser();
        try {
            user.setId(new Id(serverResponse.getMessage().getLong("uid")));
            YieldsApplication.setUser(user);
            ServiceRequest groupListRequest = new UserGroupListRequest(user);
            mService.sendRequest(groupListRequest);
            ServiceRequest userInfoRequest = new UserInfoRequest(user, user.getId());
            mService.sendRequest(userInfoRequest);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Connects to the server
     */
    private void connectToServer() {
        try {
            final ConnectionManager connectionManager = new ConnectionManager(
                    new YieldsSocketProvider());
            mCommunicationChannel = connectionManager.getCommunicationChannel();

            new Thread(new ServerListener() {
                public void run() {
                    connectionManager.subscribeToConnection(this);
                }
            }).start();

            isConnecting.set(false);
            mService.onServerConnected();

        } catch (IOException e) {
            isConnecting.set(false);
            handleConnectionError(e);
        }
    }

    private void handleGroupMessageResponse(Response serverResponse) {
        try {
            JSONObject jsonObject = serverResponse.getMetadata();
            String time = jsonObject.getString("dateTime");
            Date date = DateSerialization.dateSerializer.toDate(time);
            mCacheHelper.updateMessageStatus(Message.MessageStatus.NOT_SENT, Message.MessageStatus.SENT, date);
            //TODO : Notify app
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void handleGroupHistoryResponse(Response serverResponse) {
        try {
            JSONArray array = serverResponse.getMessage().getJSONArray("nodes");
            if (array.length() > 0) {
                ArrayList<Message> list = new ArrayList<>();
                Message message;
                Id groupId = new Id(serverResponse.getMessage().getLong("nid"));
                for (int i = 0; i < array.length(); i++) {
                    message = new Message(array.getJSONArray(i));
                    list.add(message);
                    mCacheHelper.addMessage(message, groupId);
                }
                mService.receiveMessages(groupId, list);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        } catch (CacheDatabaseException e) {
            //TODO : Decice what happens when cache adding failed.
        }
    }


    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserGroupListRequest(UserGroupListRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            List<Group> groups = mCacheHelper.getAllGroups();
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserEntourageRemoveRequest(UserEntourageRemoveRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.updateUser(serviceRequest.getUserToRemove());
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserInfoRequest(UserInfoRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mCacheHelper.getUser(serviceRequest.getUserInfoId());
        //TODO : Notify app
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupCreateRequest(GroupCreateRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.addGroup(serviceRequest.getGroup());
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }

        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserEntourageAddRequest(UserEntourageAddRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.addUser(serviceRequest.getUserToAdd());
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupUpdateVisibilityRequest(GroupUpdateVisibilityRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mCacheHelper.updateGroupVisibility(serviceRequest.getGroupId(), serviceRequest.getNewGroupVisibility());
        //TODO : Notify app
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupUpdateImageRequest(GroupUpdateImageRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mCacheHelper.updateGroupImage(serviceRequest.getGroupId(), serviceRequest.getNewGroupImage());
        //TODO : Notify app
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupUpdateNameRequest(GroupUpdateNameRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mCacheHelper.updateGroupName(serviceRequest.getGroupId(), serviceRequest.getNewGroupName());
        //TODO : Notify app
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupAddRequest(GroupAddRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.addUserToGroup(serviceRequest.getGroupId(), serviceRequest.getUser());
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupRemoveRequest(GroupRemoveRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        mCacheHelper.removeUserFromGroup(serviceRequest.getGroupId(), serviceRequest.getUserToRemoveId());
        //TODO : Notify app

        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserUpdateRequest(UserUpdateRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.updateUser(serviceRequest.getUser());
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserConnectRequest(ServiceRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handlePingRequest(ServiceRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleNodeMessageRequest(NodeMessageRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.addMessage(serviceRequest.getMessage(), serviceRequest.getReceivingNode().getId());
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }

        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleNodeHistoryRequest(GroupHistoryRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.getMessagesForGroup(serviceRequest.getGroup(),
                    serviceRequest.getDate(), GroupHistoryRequest.MESSAGE_COUNT);
            //TODO : Notify app
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }

        if (isConnected()) {
            try {
                mCommunicationChannel.sendRequest(serverRequest);
            } catch (IOException e) {
                mService.receiveError("No connection available : " + e.getMessage());
            }
        }
    }

    /**
     * Listener for the Server.
     */
    private abstract class ServerListener implements Runnable, ConnectionSubscriber {

        @Override
        public void updateOn(Response response) {
            handleServerResponse(response);
        }

        @Override
        public void updateOnConnectionProblem(IOException exception) {
            handleConnectionError(exception);
        }

        @Override
        public void updateOnParsingProblem(JSONException exception) {
            mService.receiveError("Received wrong input from server (should I be sharing this ?)");
        }
    }

}
