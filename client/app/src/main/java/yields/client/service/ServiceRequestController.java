package yields.client.service;

import android.content.res.Resources;
import android.util.Log;
import android.widget.ArrayAdapter;

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
import yields.client.node.Node;
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

import static java.lang.Thread.sleep;

//TODO : Do database calls for response handling

/**
 * Controller for ServiceRequests.
 */
public class ServiceRequestController {

    private final String TAG = "RequestController";
    private final CacheDatabaseHelper mCacheHelper;
    private final YieldService mService;
    private final AtomicBoolean isConnecting;
    // Not final because we may have to recreate it in case of connection error
    private CommunicationChannel mCommunicationChannel;
    private Thread mConnector;

    public ServiceRequestController(CacheDatabaseHelper cacheDatabaseHelper, YieldService service) {
        mCacheHelper = cacheDatabaseHelper;
        mCacheHelper.clearDatabase();
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
                        sleep(60000);
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
                handleNodeHistoryResponse(serverResponse); /* Done */
                break;
            case NODE_MESSAGE_RESPONSE:
                handleNodeMessageResponse(serverResponse); /* DONE */
                break;
            case USER_CONNECT_RESPONSE:
                handleUserConnectResponse(serverResponse); /* DONE */
                break;
            case USER_GROUP_LIST_RESPONSE:
                handleUserGroupListResponse(serverResponse); /* DONE */
                break;
            case USER_INFO_RESPONSE:
                handleUserInfoResponse(serverResponse); /* DONE */
                break;
            case GROUP_CREATE_RESPONSE:
                handleGroupCreateResponse(serverResponse); /* DONE */
                break;
            case USER_UPDATE_RESPONSE:
                handleUserUpdateResponse(serverResponse);
                break;
            case USER_SEARCH_RESPONSE:
                handleUserSearchResponse(serverResponse);
                break;
            case NODE_SEARCH_RESPONSE:
                handleNodeSearchResponse(serverResponse);
                break;
            case GROUP_UPDATE_RESPONSE:
                handleGroupUpdateResponse(serverResponse);
                break;
            case GROUP_INFO_RESPONSE:
                handleGroupInfoResponse(serverResponse);
                break;
            case GROUP_MESSAGE_RESPONSE:
                handleGroupMessageResponse(serverResponse);
                break;
            case PUBLISHER_CREATE_RESPONSE:
                handlePublisherCreateResponse(serverResponse);
                break;
            case PUBLISHER_UPDATE_RESPONSE:
                handlePublisherUpdateResponse(serverResponse);
                break;
            case PUBLISHER_INFO_RESPONSE:
                handlePublisherInfoResponse(serverResponse);
                break;
            case PUBLISHER_MESSAGE_RESPONSE:
                handlePublisherMessageResponse(serverResponse);
                break;
            case RSS_CREATE_RESPONSE:
                handleRSSCreateResponse(serverResponse);
                break;
            case NODE_MESSAGE_BCAST:
                handleNodeMessageBroadcast(serverResponse);
                break;
            case USER_UPDATE_BCAST:
                handleUserUpdateBroadcast(serverResponse);
                break;
            case GROUP_CREATE_BCAST:
                handleGroupCreateBroadcast(serverResponse);
                break;
            case GROUP_UPDATE_BCAST:
                handleGroupUpdateBroadcast(serverResponse);
                break;
            case GROUP_MESSAGE_BCAST:
                handleGroupMessageBroadcast(serverResponse);
                break;
            case PUBLISHER_CREATE_BCAST:
                handlePublisherCreateBroadcast(serverResponse);
                break;
            case PUBLISHER_UPDATE_BCAST:
                handlePublisherUpdateBroadcast(serverResponse);
                break;
            case PUBLISHER_MESSAGE_BCAST:
                handlePublisherMessageBroadcast(serverResponse);
                break;
            case RSS_CREATE_BCAST:
                handleRSSCreateBroadcast(serverResponse);
                break;
            case RSS_MESSAGE_BCAST:
                handleRSSMessageBroadcast(serverResponse);
                break;
        }
    }

    private void handleUserUpdateResponse(Response serverResponse){
        // Nothing to parse.
        // TODO : decide what to do.
    }

    private void handleUserSearchResponse(Response serverResponse){
        try {
            JSONObject response = serverResponse.getMessage();
            long uid = response.getLong("uid");
            String name = response.getString("name");
            // TODO : Parse pic, Array[Blob] ???

            // TODO : (Nico) Update right Activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleNodeSearchResponse(Response serverResponse){
        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray nodes = response.getJSONArray("nodes");
            JSONArray names = response.getJSONArray("names");
            JSONArray pic = response.getJSONArray("pic");

            assert (nodes.length() == names.length() && nodes.length() == pic.length());
            int nodeCount = nodes.length();
            ArrayList<Node> nodeList = new ArrayList<>();
            for (int i = 0 ; i < nodeCount ; i ++){
                // TODO : Make the fucking class representing nodes having an image.
                // nodeList add new Node (id image)

                // TODO : parse pics.
            }

            // TODO : (Nico) Notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleGroupUpdateResponse(Response serverResponse){
        // No output.
        // TODO : decide what to do.
    }

    private void handleGroupInfoResponse(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            Byte[] pic = (Byte[]) response.get("pic");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            // TODO : What to to now ? Which Object should I create ?
            // TODO : (Nico) Notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleGroupMessageResponse(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Date datetime = DateSerialization.dateSerializer.toDate(response.getString("datetime"));

            // TODO : (Nico) notify Activity.
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handlePublisherCreateResponse(Response serverResponse){
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nod");

            // TODO : (Nico) notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handlePublisherUpdateResponse(Response serverResponse){
        // Nothing to parse.
        // TODO : decide what to do.
    }

    private void handlePublisherInfoResponse(Response serverResponse){
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            Byte[] pic = (Byte[]) response.get("pic");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            // TODO : Create the instance when declared.
            // TODO : (Nico) Notifiy activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handlePublisherMessageResponse(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Date datetime = DateSerialization.dateSerializer.toDate(response.getString("datetime"));

            // TODO : (Nico) Notify Activtiy .
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleRSSCreateResponse(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            // TODO (Nico) : Notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleNodeMessageBroadcast(Response serverResponse){
        // TODO : Not very clear on how to use this. See later.
    }

    private void handleUserUpdateBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            String name = response.getString("name");
            Byte[] pic = (Byte[]) response.get("pic");

            // TODO : (Nico) Notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleGroupCreateBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            // TODO : (Nico) Notify app.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleGroupUpdateBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            Byte[] pic = (Byte[]) response.get("pic");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            // TODO : (Nico) Notify app.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleGroupMessageBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();

            long nid = response.getLong("nid");
            Date datetime = DateSerialization.dateSerializer.toDate(response.getString("datetime"));
            long senderId = response.getLong("sender");
            String text = response.getString("text");
            String contentType = response.getString("contentType");
            Byte[] content = (Byte[]) response.get("content");

            // TODO : Create Message.
            // TODO : (Nico) Notify activity.
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handlePublisherCreateBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            // TODO : Create instance (Not yet declared topkek)
            // TODO : (Nico) notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handlePublisherUpdateBroadcast(Response serverResponse){

    }

    private void handlePublisherMessageBroadcast(Response serverResponse){

    }

    private void handleRSSCreateBroadcast(Response serverResponse){

    }

    private void handleRSSMessageBroadcast(Response serverResponse){

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
        /*try {
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
                //
                //YieldsApplication.getUser()
                //    .addUserToEntourage(new User(serverResponse.getMessage()));
                //
            }

        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }*/

        try {
            JSONObject response = serverResponse.getMessage();
            if (YieldsApplication.getUser().getId().getId().equals(-1)){
                YieldsApplication.getUser().update(response);
                JSONArray entourage = response.getJSONArray("entourage");
                JSONArray entourageRefreshedAt = response.getJSONArray("entourageUpdatedAt");

                if (entourage != null && entourageRefreshedAt != null){
                    for (int i = 0; i < entourage.length(); i++) {
                        // TODO : Improve this, add field in user  ?
                        if (DateSerialization.dateSerializer.toDate(entourageRefreshedAt.getString(i)
                        ).compareTo(new Date()) == -1){
                            ServiceRequest userInfoRequest = new UserInfoRequest(YieldsApplication.getUser(),
                                    new Id(entourage.getLong(i)));
                            mService.sendRequest(userInfoRequest);
                        }
                    }
                }
            }
            else {
                // TODO
            }
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    private void handleUserGroupListResponse(Response serverResponse) {
        /*try {
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
        }*/

        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray groups = response.getJSONArray("groups");
            JSONArray names = response.getJSONArray("names");
            JSONArray updatedAt = response.getJSONArray("updatedAt");
            JSONArray refreshedAt = response.getJSONArray("refreshedAt");

            int groupCount = groups.length();
            assert (groupCount == names.length() && groupCount == updatedAt.length() &&
                    groupCount == refreshedAt.length());

            for (int i = 0 ; i < groupCount ; i ++){
                Group group = new Group(groups.getString(i), names.getString(i), refreshedAt
                        .getString(i));
                YieldsApplication.getUser().addGroup(group);
                ServiceRequest historyRequest = new GroupHistoryRequest(group, new Date());
                mService.sendRequest(historyRequest);
            }
            mService.notifyChange();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

    }

    private void handleUserConnectResponse(Response serverResponse) {
        // TODO : Parse the new field.
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

    /**
     * Handles NodeMessage responses.
     *
     * @param serverResponse The Response received from the server.
     */
    private void handleNodeMessageResponse(Response serverResponse) {
        // TODO : Handle errors JSONs
        try {
            JSONObject responseMessage = serverResponse.getMessage();
            JSONObject metadata = serverResponse.getMetadata();
            String time = metadata.getString("ref");
            Date date = DateSerialization.dateSerializer.toDate(time);
            Group group = mCacheHelper.getGroup(new Id(Long.valueOf(metadata.getString("client"))));
            Message message = mCacheHelper.getMessagesForGroup(group, date, 1).get(0);
            mCacheHelper.deleteMessage(message, group.getId());
            Message updatedMessage = new Message("", new Id(-1), message.getSender(), message.getContent(),
                    DateSerialization.dateSerializer.toDate(metadata.getString("datetime")),
                    Message.MessageStatus.SENT);
            mCacheHelper.addMessage(updatedMessage, group.getId());
            mService.updateMessage(group, updatedMessage, date);
        } catch (JSONException | ParseException | CacheDatabaseException e) {
            Log.d(TAG, "Couldn't handle NodeMessageResponse correctly !");
        }
    }

    /**
     * Handles NodeHistory responses.
     *
     * @param serverResponse The response received from the server.
     */
    private void handleNodeHistoryResponse(Response serverResponse) {
        /*try {
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
        } catch (JSONException | ParseException | CacheDatabaseException e) {
            Log.d(TAG, "Couldn't handle NodeMessageResponse correctly !");
        }*/

        try {
            long nid = serverResponse.getMessage().getLong("nid");
            JSONArray datetimes = serverResponse.getMessage().getJSONArray("datetimes");
            JSONArray senders = serverResponse.getMessage().getJSONArray("senders");
            JSONArray texts = serverResponse.getMessage().getJSONArray("texts");
            JSONArray contentTypes = serverResponse.getMessage().getJSONArray("contentTypes");
            JSONArray contents = serverResponse.getMessage().getJSONArray("contents");

            int count = datetimes.length();
            assert (count == senders.length() && count == texts.length() && count == contentTypes
                    .length() && count == contents.length());

            Id groupId = new Id(nid);
            ArrayList<Message> messageList = new ArrayList<>();
            for (int i = 0 ; i < count ; i ++){
                Message message = new Message(datetimes.getString(i), senders.getString(i), texts
                        .getString(i), contentTypes.getString(i), (Byte[]) contents.get(i));
                messageList.add(message);
                mCacheHelper.addMessage(message, groupId);
            }

            mService.receiveMessages(groupId, messageList);
        } catch (JSONException | ParseException | CacheDatabaseException e) {
            e.printStackTrace();
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
            Log.d(TAG, "Couldn't handle UserGroupListRequest correctly !");
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
            Log.d(TAG, "Couldn't handle UserEntourageRemove correctly !");
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
            Log.d(TAG, "Couldn't handle GroupCreateRequest correctly !");
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
            Log.d(TAG, "Couldn't handle UserEntourageAddRequest correctly !");
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
            Log.d(TAG, "Couldn't handle handleGroupAddRequest correctly !");
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
            Log.d(TAG, "Couldn't handle UserUpdateRequest correctly !");
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
            Log.d(TAG, "Couldn't handle NodeMessageRequest correctly !");
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
            List<Message> messages = mCacheHelper.getMessagesForGroup(serviceRequest.getGroup(),
                    serviceRequest.getDate(), GroupHistoryRequest.MESSAGE_COUNT);
            mService.receiveMessages(serviceRequest.getGroup().getId(), messages);
        } catch (CacheDatabaseException e) {
            Log.d(TAG, "Couldn't handle NodeHistoryRequest correctly !");
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
