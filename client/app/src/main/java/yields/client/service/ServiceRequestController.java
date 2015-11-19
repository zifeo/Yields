package yields.client.service;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import yields.client.cache.CacheDatabaseHelper;
import yields.client.exceptions.CacheDatabaseException;
import yields.client.exceptions.ServiceRequestException;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.serverconnection.CommunicationChannel;
import yields.client.serverconnection.ConnectionManager;
import yields.client.serverconnection.ConnectionSubscriber;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;
import yields.client.serverconnection.YieldEmulatorSocketProvider;
import yields.client.servicerequest.GroupHistoryRequest;
import yields.client.servicerequest.NodeMessageRequest;
import yields.client.servicerequest.ServiceRequest;

/**
 * Controller for ServiceRequests.
 */
public class ServiceRequestController {

    private final CacheDatabaseHelper mCacheHelper;
    private final YieldService mService;
    // Not final because we may have to recreate it in case of connection error
    private CommunicationChannel mCommunicationChannel;

    public ServiceRequestController(CacheDatabaseHelper cacheDatabaseHelper, YieldService service) {
        mCacheHelper = cacheDatabaseHelper;
        mService = service;

        try {
            final ConnectionManager connectionManager = new ConnectionManager(
                    new YieldEmulatorSocketProvider());
            mCommunicationChannel = connectionManager.getCommunicationChannel();

            new Thread(new ServerListener() {
                public void run() {
                    connectionManager.subscribeToConnection(this);
                }
            }).start();

        } catch (IOException e) {
            handleConnectionError(e);
        }

    }

    public void handleConnectionError(IOException e){
        mService.receiveError("Problem connecting to server : " + e.getMessage());
    }

    /**
     * Handles any given ServiceRequest.
     * @param serviceRequest
     */
    public void handleServiceRequest(ServiceRequest serviceRequest) {
        switch (serviceRequest.getType()) {
            case PING:
                handlePingRequest();
                break;
            case USER_CONNECT:
                handleUserConnectRequest();
                break;
            case USER_UPDATE:
                handleUserUpdateRequest();
                break;
            case USER_GROUP_LIST:
                handleUserGroupListRequest();
                break;
            case USER_ENTOURAGE_ADD:
                handleUserEntourageAddRequest();
                break;
            case USER_ENTOURAGE_REMOVE:
                handleUserEntourageRemoveRequest();
                break;
            case USER_STATUS:
                handleUserStatusRequest();
                break;
            case GROUP_CREATE:
                handleGroupCreateRequest();
                break;
            case GROUP_UPDATE_NAME:
                handleGroupUpdateNameRequest();
                break;
            case GROUP_UPDATE_VISIBILITY:
                handleGroupUpdateVisibilityRequest();
                break;
            case GROUP_UPDATE_IMAGE:
                handleGroupUpdateImageRequest();
                break;
            case GROUP_ADD:
                handleGroupAddRequest();
                break;
            case GROUP_REMOVE:
                handleGroupRemoveRequest();
                break;
            case GROUP_MESSAGE:
                handleGroupMessageRequest((NodeMessageRequest) serviceRequest);
                break;
            case GROUP_HISTORY:
                handleGroupHistoryRequest((GroupHistoryRequest) serviceRequest);
                break;
            default:
                throw new ServiceRequestException("No such ServiceRequest type !");
        }
    }

    public void handleServerResponse(Response serverResponse) {
        switch (serverResponse.getKind()) {
            case GROUPHISTORYRES:
                handleAddMessagesToGroup(serverResponse);
                break;
            default:
                throw new ServiceRequestException("No such ServiceResponse type !");
                //TODO: In need of another exception ?
        }
    }

    private void handleAddMessagesToGroup(Response response) {
        try {
            JSONArray array = response.getMessage().getJSONArray("nodes");
            if (array.length() > 0) {
                ArrayList<Message> list = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    list.add(new Message(array.getJSONArray(i)));
                }

                mService.receiveMessages(new Id(response.getMessage().getLong("nid")), list);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserGroupListRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserEntourageRemoveRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserStatusRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupCreateRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserEntourageAddRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupUpdateVisibilityRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupUpdateImageRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupUpdateNameRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupAddRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupRemoveRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserUpdateRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleUserConnectRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handlePingRequest() {
        //TODO
    }

    /**
     * Handles a ServiceRequest which is given to it by argument.
     */
    private void handleGroupMessageRequest(NodeMessageRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.addMessage(serviceRequest.getMessage(), serviceRequest.getReceivingGroup().getId());
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
    private void handleGroupHistoryRequest(GroupHistoryRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.getMessagesForGroup(serviceRequest.getGroup(),
                    serviceRequest.getDate(), GroupHistoryRequest.MESSAGE_COUNT);
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }

        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("No connection available : " + e.getMessage());
        }

        //Once response is received add all Messages to cache.
        /*
        for(Message message : receivedMessages){
            mCacheHelper.add(message, serverRequest.getGroup.getId());
        }
         */
    }

    /**
     * Listener
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
