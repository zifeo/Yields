package yields.client.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import yields.client.servicerequest.GroupMessageRequest;
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
        mService.receiveError("problem connecting to server : " + e.getMessage());
    }

    public void handleServiceRequest(ServiceRequest serviceRequest) {
        switch (serviceRequest.getType()) {
            case PING:
                handlePingRequest();
                break;
            case USERCONNECT:
                handleUserConnectRequest();
                break;
            case USERUPDATE:
                handleUserUpdateRequest();
                break;
            case USERGROUPLIST:
                handleUserGroupListRequest();
                break;
            case USERENTOURAGEADD:
                handleUserEntourageAddRequest();
                break;
            case USERENTOURAGEREMOVE:
                handleUserEntourageRemoveRequest();
                break;
            case USERSTATUS:
                handleUserStatusRequest();
                break;
            case GROUPCREATE:
                handleGroupCreateRequest();
                break;
            case GROUPUPDATENAME:
                handleGroupUpdateNameRequest();
                break;
            case GROUPUPDATEVISIBILITY:
                handleGroupUpdateVisibilityRequest();
                break;
            case GROUPUPDATEIMAGE:
                handleGroupUpdateImageRequest();
                break;
            case GROUPADD:
                handleGroupAddRequest();
                break;
            case GROUPREMOVE:
                handleGroupRemoveRequest();
                break;
            case GROUPMESSAGE:
                handleGroupMessageRequest((GroupMessageRequest) serviceRequest);
                break;
            case GROUPHISTORY:
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

    private void handleUserGroupListRequest() {
        //TODO
    }

    private void handleUserEntourageRemoveRequest() {

    }

    private void handleUserStatusRequest() {

    }

    private void handleGroupCreateRequest() {

    }

    private void handleUserEntourageAddRequest() {

    }

    private void handleGroupUpdateVisibilityRequest() {

    }

    private void handleGroupUpdateImageRequest() {

    }

    private void handleGroupUpdateNameRequest() {

    }

    private void handleGroupAddRequest() {

    }

    private void handleGroupRemoveRequest() {

    }

    private void handleUserUpdateRequest() {

    }

    private void handleUserConnectRequest() {
    }

    private void handlePingRequest() {

    }

    private void handleGroupMessageRequest(GroupMessageRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.addMessage(serviceRequest.getMessage(), serviceRequest.getReceivingGroup().getId());
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }

        try {
            mCommunicationChannel.sendRequest(serverRequest);
        } catch (IOException e) {
            mService.receiveError("no connection available : " + e.getMessage());
        }
    }

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
            mService.receiveError("no connection available : " + e.getMessage());
        }

        //Once response is received add all Messages to cache.
        /*
        for(Message message : receivedMessages){
            mCacheHelper.add(message, serverRequest.getGroup.getId());
        }
         */

        //ALL NODE ID'S MOST BE UNIQUE, OR ELSE WHEN ADDING TO CACHE ALL PREVIOUS
        //NODES WITH SAME ID ARE DELETED
        //THIS CAN BE CHANGED EASILY
        //BUT I'D APPRECIATE NOT HAVING TO CHANGE MY IMPLEMENTATION EVRY OTHER WEEK
        //K THX BYE
    }

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
