package yields.client.service;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import yields.client.cache.CacheDatabaseHelper;
import yields.client.serverconnection.CommunicationChannel;
import yields.client.serverconnection.ConnectionManager;
import yields.client.serverconnection.ConnectionSubscriber;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;
import yields.client.serverconnection.YieldsSocketProvider;
import yields.client.servicerequest.GroupCreateRequest;
import yields.client.servicerequest.GroupUpdateTagsRequest;
import yields.client.servicerequest.GroupMessageRequest;
import yields.client.servicerequest.GroupUpdateImageRequest;
import yields.client.servicerequest.GroupUpdateNameRequest;
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

import static java.lang.Thread.sleep;

/**
 * Controller for ServiceRequests and ServerResponses.
 */
public class ServiceRequestController {

    private final ResponseHandler mResponseHandler;
    private final RequestHandler mRequestHandler;
    private final String TAG = "RequestController";
    private final CacheDatabaseHelper mCacheHelper;
    private final YieldService mService;
    private final AtomicBoolean isConnecting;
    // Not final because we may have to recreate it in case of connection error
    private CommunicationChannel mCommunicationChannel;
    private Thread mConnector;

    /**
     * Constructs the requestController which will serve as a link to the server and cache.
     *
     * @param cacheDatabaseHelper The cache helper that will be used for cache handling.
     * @param service             The service that is using this Controller.
     */
    public ServiceRequestController(CacheDatabaseHelper cacheDatabaseHelper, YieldService service) {
        mCacheHelper = cacheDatabaseHelper;
        mCacheHelper.clearDatabase();
        mService = service;
        isConnecting = new AtomicBoolean(true);
        connectToServer();
        mResponseHandler = new ResponseHandler(mCacheHelper, mService);
        mRequestHandler = new RequestHandler(mCacheHelper, mService, this);
    }

    /**
     * Handles any error while connecting to the server.
     *
     * @param e the exception that was triggered the connection error.
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
     * Notify connector to reconnect faster.
     */
    public void notifyConnector() {
        if (mConnector != null && mConnector.isAlive()) {
            mConnector.interrupt();
        }
    }

    /**
     * Test if the server is connected.
     *
     * @return True if is connected, false otherwise.
     */
    public boolean isConnected() {
        return !isConnecting.get();
    }

    /**
     * Handles any given ServiceRequest.
     *
     * @param serviceRequest The serviceRequest that should be handled.
     */
    public void handleServiceRequest(ServiceRequest serviceRequest) {
        switch (serviceRequest.getType()) {
            case PING:
                mRequestHandler.handlePingRequest(serviceRequest);
                break;
            case USER_CONNECT:
                mRequestHandler.handleUserConnectRequest(serviceRequest);
                break;
            case RSS_CREATE:
                mRequestHandler.handleRssCreateRequest((RSSCreateRequest) serviceRequest);
                break;
            case USER_UPDATE:
                mRequestHandler.handleUserUpdateRequest((UserUpdateRequest) serviceRequest);
                break;
            case USER_UPDATE_NAME:
                mRequestHandler.handleUserUpdateNameRequest((UserUpdateNameRequest) serviceRequest);
                break;
            case USER_NODE_LIST:
                mRequestHandler.handleUserGroupListRequest((UserGroupListRequest) serviceRequest);
                break;
            case USER_ENTOURAGE_ADD:
                mRequestHandler.handleUserEntourageAddRequest((UserEntourageAddRequest) serviceRequest);
                break;
            case USER_ENTOURAGE_REMOVE:
                mRequestHandler.handleUserEntourageRemoveRequest((UserEntourageRemoveRequest) serviceRequest);
                break;
            case USER_INFO:
                mRequestHandler.handleUserInfoRequest((UserInfoRequest) serviceRequest);
                break;
            case GROUP_CREATE:
                mRequestHandler.handleGroupCreateRequest((GroupCreateRequest) serviceRequest);
                break;
            case GROUP_INFO:
                mRequestHandler.handleGroupInfoRequest(serviceRequest);
                break;
            case GROUP_UPDATE_NAME:
                mRequestHandler.handleGroupUpdateNameRequest((GroupUpdateNameRequest) serviceRequest);
                break;
            case GROUP_UPDATE_IMAGE:
                mRequestHandler.handleGroupUpdateImageRequest((GroupUpdateImageRequest) serviceRequest);
                break;
            case GROUP_UPDATE_USERS:
                mRequestHandler.handleGroupUpdateUsersRequest((GroupUpdateUsersRequest) serviceRequest);
                break;
            case GROUP_UPDATE_NODES:
                mRequestHandler.handleGroupUpdateNodesRequest((GroupUpdateTagsRequest) serviceRequest);
                break;
            case GROUP_MESSAGE:
                mRequestHandler.handleNodeMessageRequest((GroupMessageRequest) serviceRequest);
                break;
            case NODE_HISTORY:
                mRequestHandler.handleNodeHistoryRequest((NodeHistoryRequest) serviceRequest);
                break;
            case MEDIA_MESSAGE:
                mRequestHandler.handleMediaMessageRequest((MediaMessageRequest) serviceRequest);
                break;
            case USER_SEARCH:
                mRequestHandler.handleUserSearchRequest((UserSearchRequest) serviceRequest);
                break;
            case NODE_SEARCH:
                mRequestHandler.handleNodeSearchRequest((NodeSearchRequest) serviceRequest);
                break;
            default:
                Log.d("Y:" + this.getClass().getName(), "No such ServiceRequest type ! : " +
                        serviceRequest.getType());
        }
    }

    /**
     * Handles any given Response.
     *
     * @param serverResponse The response to be handled.
     */
    public void handleServerResponse(Response serverResponse) {
        switch (serverResponse.getKind()) {
            case NODE_HISTORY_RESPONSE:
                mResponseHandler.handleNodeHistoryResponse(serverResponse); /* Done */
                break;
            case USER_CONNECT_RESPONSE:
                mResponseHandler.handleUserConnectResponse(serverResponse); /* DONE */
                break;
            case USER_NODE_LIST_RESPONSE:
                mResponseHandler.handleUserGroupListResponse(serverResponse); /* DONE */
                break;
            case USER_INFO_RESPONSE:
                mResponseHandler.handleUserInfoResponse(serverResponse); /* DONE */
                break;
            case GROUP_CREATE_RESPONSE:
                mResponseHandler.handleGroupCreateResponse(serverResponse); /* DONE */
                break;
            case USER_SEARCH_RESPONSE:
                mResponseHandler.handleUserSearchResponse(serverResponse);
                break;
            case NODE_SEARCH_RESPONSE:
                mResponseHandler.handleNodeSearchResponse(serverResponse);
                break;
            case GROUP_INFO_RESPONSE:
                mResponseHandler.handleGroupInfoResponse(serverResponse);
                break;
            case GROUP_MESSAGE_RESPONSE:
                mResponseHandler.handleGroupMessageResponse(serverResponse);
                break;
            case PUBLISHER_CREATE_RESPONSE:
                mResponseHandler.handlePublisherCreateResponse(serverResponse);
                break;
            case PUBLISHER_UPDATE_RESPONSE:
                mResponseHandler.handlePublisherUpdateResponse(serverResponse);
                break;
            case PUBLISHER_INFO_RESPONSE:
                mResponseHandler.handlePublisherInfoResponse(serverResponse);
                break;
            case PUBLISHER_MESSAGE_RESPONSE:
                mResponseHandler.handlePublisherMessageResponse(serverResponse);
                break;
            case RSS_CREATE_RESPONSE:
                mResponseHandler.handleRSSCreateResponse(serverResponse);
                break;
            case USER_UPDATE_BCAST:
                mResponseHandler.handleUserUpdateBroadcast(serverResponse);
                break;
            case GROUP_CREATE_BCAST:
                mResponseHandler.handleGroupCreateBroadcast(serverResponse);
                break;
            case GROUP_UPDATE_BCAST:
                mResponseHandler.handleGroupUpdateBroadcast(serverResponse);
                break;
            case NODE_MESSAGE_BCAST:
                mResponseHandler.handleNodeMessageBroadcast(serverResponse);
                break;
            case PUBLISHER_CREATE_BCAST:
                mResponseHandler.handlePublisherCreateBroadcast(serverResponse);
                break;
            case PUBLISHER_UPDATE_BCAST:
                mResponseHandler.handlePublisherUpdateBroadcast(serverResponse);
                break;
            case RSS_CREATE_BCAST:
                mResponseHandler.handleRSSCreateBroadcast(serverResponse);
                break;
            case MEDIA_MESSAGE_RESPONSE:
                mResponseHandler.handleMediaMessageResponse(serverResponse);
                break;
            case RSS_INFO_RES:
                mResponseHandler.handleRSSInfoResponse(serverResponse);
                break;
            default:
                Log.d("Y:" + this.getClass().getName(), "No such response kind : " +
                        serverResponse.getKind());
        }
    }

    /**
     * Connects the controller to the server.
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
     * Sends a ServerRequest to the server.
     *
     * @param serverRequest The ServerRequest that should be sent.
     */
    protected void sendToServer(ServerRequest serverRequest) {
        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
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
