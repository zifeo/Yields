package yields.client.service;


import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yields.client.activities.MockFactory;
import yields.client.activities.NotifiableActivity;
import yields.client.cache.CacheDatabaseHelper;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;

import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.ImageSerialization;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;
import yields.client.service.RequestHandler;
import yields.client.service.ServiceRequestController;
import yields.client.service.YieldService;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserGroupListRequest;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class RequestHandlerTests {
    private static MockYieldsService mService;
    private static MockCacheDatabaseHelper mCacheDatabaseHelper;
    private static MockServiceRequestController mServiceRequestController;
    private SQLiteDatabase mDatabase;

    @Before
    public void setUp(){

        YieldsApplication.setApplicationContext(InstrumentationRegistry.getTargetContext());
        YieldsApplication.setResources(InstrumentationRegistry.getTargetContext().getResources());
        YieldsApplication.setUser(MockFactory.generateFakeClientUser(
                "Bobby", new Id(123), "lol@gmail.com", YieldsApplication.getDefaultGroupImage()));
        CacheDatabaseHelper.deleteDatabase();
        mCacheDatabaseHelper = new MockCacheDatabaseHelper();
        mDatabase = mCacheDatabaseHelper.getWritableDatabase();
        mCacheDatabaseHelper.clearDatabase();
        mService = new MockYieldsService();
        mServiceRequestController = new MockServiceRequestController(mCacheDatabaseHelper, mService);
        YieldsApplication.setUser(
                new ClientUser("Johny", new Id(999999), "topKeke@gmail.com", YieldsApplication.getDefaultUserImage()));

    }

    @Test
    public void testHandleUserGroupListRequest(){
        UserGroupListRequest request = new UserGroupListRequest(YieldsApplication.getUser());
        RequestHandler handler = createRequestHandler();
        handler.handleUserGroupListRequest(request);
        assertEquals(NotifiableActivity.Change.GROUP_LIST, mService.mLastChange);
        Log.d("RequestHndlrTests", mServiceRequestController.mLastRequest.message());
    }

    private RequestHandler createRequestHandler(){
        return new RequestHandler(mCacheDatabaseHelper, mService, mServiceRequestController);
    }

    private class MockYieldsService extends YieldService{
        public NotifiableActivity.Change mLastChange;

        public MockYieldsService(){
            super();
        }

        @Override
        public void notifyChange(NotifiableActivity.Change change){
            //super.notifyChange(change);
            Log.d("MockYieldService", "changeeeeeeeeeeeeeeee");
            mLastChange = change;
        }
    }

    private class MockCacheDatabaseHelper extends CacheDatabaseHelper{
        public MockCacheDatabaseHelper(){
            super();
        }
    }

    private class MockServiceRequestController extends ServiceRequestController{
        public ServerRequest mLastRequest;
        /**
         * Constructs the requestController which will serve as a link to the server and cache.
         *
         * @param cacheDatabaseHelper The cache helper that will be used for cache handling.
         * @param service             The service that is using this Controller.
         */
        public MockServiceRequestController(CacheDatabaseHelper cacheDatabaseHelper, YieldService service) {
            super(cacheDatabaseHelper, service);
        }

        @Override
        protected void sendToServer(ServerRequest serverRequest) {
           // super.sendToServer(serverRequest);
            mLastRequest = serverRequest;
        }
    }
}
