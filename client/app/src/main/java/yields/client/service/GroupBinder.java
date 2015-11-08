package yields.client.service;

import android.os.Binder;

public class GroupBinder extends Binder {
    private final YieldService mService;

    /**
     * Creates the binder and links it to the service
     * @param service The Service concerned
     */
    public GroupBinder(YieldService service) {
        mService = service;
    }
}
