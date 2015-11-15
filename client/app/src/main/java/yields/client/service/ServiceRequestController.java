package yields.client.service;

import yields.client.cache.CacheDatabaseHelper;
import yields.client.exceptions.CacheDatabaseException;
import yields.client.exceptions.ServiceRequestException;
import yields.client.serverconnection.ServerRequest;
import yields.client.servicerequest.GroupHistoryRequest;
import yields.client.servicerequest.GroupMessageRequest;
import yields.client.servicerequest.ServiceRequest;

/**
 * Controller for ServiceRequests.
 */
public class ServiceRequestController {

    private final CacheDatabaseHelper mCacheHelper;

    public ServiceRequestController(CacheDatabaseHelper cacheDatabaseHelper) {
        mCacheHelper = cacheDatabaseHelper;
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
            //TODO : Decide what happens if cache adding failed.
        }
        //TODO :
    }

    private void handleGroupHistoryRequest(GroupHistoryRequest serviceRequest) {

    }
}
