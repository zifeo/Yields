package yields.client.generalhelpers;

import android.graphics.Bitmap;

import yields.client.id.Id;
import yields.client.node.ClientUser;
import yields.client.yieldsapplication.YieldsApplication;

public class MockModel {

    public MockModel() {
        YieldsApplication.setUser(new ClientUser("GCC", new Id(2), "test@bluewin.ch", Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565)));
    }
}
