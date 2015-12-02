package yields.client.generalhelpers;

import yields.client.node.ClientUser;
import yields.client.yieldsapplication.YieldsApplication;

public class MockModel {

    public MockModel() {
        YieldsApplication.setUser(new ClientUser("test@bluewin.ch"));
    }
}
