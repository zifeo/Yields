package yields.client.service;

import android.util.Log;

import java.io.IOException;

import yields.client.cache.CacheDatabaseHelper;
import yields.client.exceptions.CacheDatabaseException;
import yields.client.exceptions.ServiceRequestException;
import yields.client.messages.Message;
import yields.client.serverconnection.CommunicationChannel;
import yields.client.serverconnection.ConnectionManager;
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
            ConnectionManager connectionManager = new ConnectionManager(
                    new YieldEmulatorSocketProvider());
            mCommunicationChannel = connectionManager.getCommunicationChannel();
        } catch (IOException e) {
            mService.receiveError("problem connecting to server : " + e.getMessage());
        }

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
}
