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

    /**
     * Handles any given ServiceRequest.
     * @param serviceRequest
     */
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
        //TODO : notifyApp();
        //TODO : Send serverRequest to Server
        //Once response is received add all Messages to cache.
        /*
        for(Message message : receivedMessages){
            mCacheHelper.add(message, serverRequest.getGroup.getId());
        }
         */
    }
}
