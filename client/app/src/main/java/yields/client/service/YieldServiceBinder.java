package yields.client.service;

import android.os.Binder;
import android.util.Log;

import java.util.Objects;

import yields.client.activities.NotifiableActivity;
import yields.client.servicerequest.ServiceRequest;

public class YieldServiceBinder extends Binder {
    private final YieldService mService;
    public NotifiableActivity.Change connected = null;

    /**
     * Creates the binder and links it to the service
     * @param service The Service concerned
     */
    public YieldServiceBinder(YieldService service) {
        Objects.requireNonNull(service);
        mService = service;
    }

    /**
     * Binds the activity to the service which permits it to receive incoming messages directly
     * @param activity the current messagingActivity
     */
    public void attachActivity(NotifiableActivity activity) {
        Objects.requireNonNull(activity);
        mService.setNotifiableActivity(activity);
    }

    public void unsetNotifiableActivity(){
        mService.unsetNotifiableActivity();
    }

    /**
     * Can be used to know if the server is connected to the server
     */
    public void connectionStatus(){
        mService.connectionStatusResponse();
    }

    /**
     * Asks the service to try and reconnect the server
     */
    public void reconnect(){
        mService.reconnectServer();
    }

    /**
     * Send a request to server via the Service.
     * @param request The request to be sent.
     */
    public void sendRequest(ServiceRequest request) {
        Objects.requireNonNull(request);
        mService.sendRequest(request);
    }

    /**
     * Apply change
     */
    public void changeStatus(NotifiableActivity.Change newStatus) {
        connected = newStatus;
    }
}
