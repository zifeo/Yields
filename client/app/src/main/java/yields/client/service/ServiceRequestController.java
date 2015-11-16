package yields.client.service;

import yields.client.cache.CacheDatabaseHelper;
import yields.client.exceptions.CacheDatabaseException;
import yields.client.exceptions.ServiceRequestException;
import yields.client.messages.Message;
import yields.client.serverconnection.ServerRequest;
import yields.client.servicerequest.GroupHistoryRequest;
import yields.client.servicerequest.GroupMessageRequest;
import yields.client.servicerequest.ServiceRequest;

/**
 * Controller for ServiceRequests.
 */
public class ServiceRequestController {

    private final CacheDatabaseHelper mCacheHelper;
    //TODO : Add connection

    public ServiceRequestController(CacheDatabaseHelper cacheDatabaseHelper) {
        mCacheHelper = cacheDatabaseHelper;
        //TODO : Set Instance of connection
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
        //TODO
    }

    private void handleUserStatusRequest() {
        //TODO
    }

    private void handleGroupCreateRequest() {
        //TODO
    }

    private void handleUserEntourageAddRequest() {
        //TODO
    }

    private void handleGroupUpdateVisibilityRequest() {
        //TODO
    }

    private void handleGroupUpdateImageRequest() {
        //TODO
    }

    private void handleGroupUpdateNameRequest() {
        //TODO
    }

    private void handleGroupAddRequest() {
        //TODO
    }

    private void handleGroupRemoveRequest() {
        //TODO
    }

    private void handleUserUpdateRequest() {
        //TODO
    }

    private void handleUserConnectRequest() {
        //TODO
    }

    private void handlePingRequest() {
        //TODO
    }

    private void handleGroupMessageRequest(GroupMessageRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.addMessage(serviceRequest.getMessage(), serviceRequest.getReceivingGroup().getId());
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }
        //TODO : notifyApp();
        //TODO : Send serverRequest to Server
    }

    private void handleGroupHistoryRequest(GroupHistoryRequest serviceRequest) {
        ServerRequest serverRequest = serviceRequest.parseRequestForServer();
        try {
            mCacheHelper.getMessagesForGroup(serviceRequest.getGroup(),
                    serviceRequest.getDate(), GroupHistoryRequest.MESSAGE_COUNT);
        } catch (CacheDatabaseException e) {
            //TODO : @Nroussel Decide what happens if cache adding failed.
        }
        //TODO : notifyApp();
        //TODO : Send serverRequest to Server
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
