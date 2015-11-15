package yields.client.service;

import yields.client.servicerequest.ServiceRequest;

/**
 * Controller for ServiceRequests.
 */
public class ServiceRequestController {


    public ServiceRequestController() {

    }

    public void handleServiceRequest(ServiceRequest serviceRequest) {
        switch (serviceRequest.getType()) {
            case GROUPMESSAGE:
                break;
            case GROUPHISTORY:
                break;
            default:
                System.out.print("hello");

        }
    }

    private void handleGroupMessageRequest(ServiceRequest serviceRequest) {

    }
}
