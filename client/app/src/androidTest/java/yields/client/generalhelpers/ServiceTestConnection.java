package yields.client.generalhelpers;

import yields.client.service.YieldServiceBinder;
import yields.client.yieldsapplication.YieldsApplication;

import static org.mockito.Mockito.mock;

public class ServiceTestConnection {

    public static void connectActivityToService(){
        YieldServiceBinder mockBinder = mock(YieldServiceBinder.class);
        YieldsApplication.setBinder(mockBinder);
    }
}
