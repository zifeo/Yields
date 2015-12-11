package yields.client.service;


import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yields.client.activities.MockFactory;
import yields.client.activities.NotifiableActivity;
import yields.client.cache.CacheDatabaseHelper;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.messages.UrlContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;

import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.ImageSerialization;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;
import yields.client.serverconnection.YieldEmulatorSocketProvider;
import yields.client.service.RequestHandler;
import yields.client.service.ServiceRequestController;
import yields.client.service.YieldService;
import yields.client.servicerequest.GroupCreateRequest;
import yields.client.servicerequest.GroupMessageRequest;
import yields.client.servicerequest.GroupUpdateImageRequest;
import yields.client.servicerequest.GroupUpdateNameRequest;
import yields.client.servicerequest.GroupUpdateUsersRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserEntourageAddRequest;
import yields.client.servicerequest.UserEntourageRemoveRequest;
import yields.client.servicerequest.UserGroupListRequest;
import yields.client.servicerequest.UserUpdateNameRequest;
import yields.client.servicerequest.UserUpdateRequest;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ResponseHandlerTests {

    private static MockYieldsService mService;
    private static MockCacheDatabaseHelper mCacheDatabaseHelper;
    private static MockServiceRequestController mServiceRequestController;
    private SQLiteDatabase mDatabase;

    private ResponseHandler mHandler;

    @Before
    public void setUp() throws ParseException {

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
                new ClientUser("Johny", new Id(10), "topKeke@gmail.com", YieldsApplication.getDefaultUserImage()));
        ArrayList<Id> list = new ArrayList<>();
        list.add(YieldsApplication.getUser().getId());
        Group group = new Group("Nice meme", new Id(10), list);
        group.addMessage(new Message("2015-12-10T22:48:23.553Z", 0l, 10l, "hello", "", ""));
        YieldsApplication.getUser().addGroup(group);
        mCacheDatabaseHelper.addGroup(group);

        mHandler = new ResponseHandler(mCacheDatabaseHelper, mService);

    }

    @Test
    public void handleUserSearchResponseTest() {

    }

    @Test
    public void testHandleUserEntourageRemoveRequest() {



        UserEntourageRemoveRequest request = new UserEntourageRemoveRequest(YieldsApplication.getUser().getId(), new
                Id(117));

        RequestHandler handler = createRequestHandler();
        handler.handleUserEntourageRemoveRequest(request);

        assertEquals(Long.valueOf(117), mCacheDatabaseHelper.mLastIdUpdated.getId());

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        Log.d("RequestHandlerTests", sReq.message());
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            // Expected :
            // {"metadata":{"client":999999,"ref":<date>,"datetime":<date>},
            // "kind":"UserUpdate","message":{"addEntourage":[],"pic":null,"name":null,"email":null,"removeEntourage":[117]}}
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals(metadata.getString("ref"), metadata.getString("datetime"));
            assertEquals("UserUpdate", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals(0, message.getJSONArray("addEntourage").length());
            log(message.get("pic").toString());
            assertEquals("null", message.getString("pic"));
            assertEquals("null", message.getString("name"));
            assertEquals("null", message.getString("email"));
            JSONArray toRemove = message.getJSONArray("removeEntourage");
            assertEquals(1, toRemove.length());
            assertEquals(117, toRemove.get(0));
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }



    @Test
    public void handleNodeSearchResponseTest() throws JSONException {
        String response = "{\"kind\":\"NodeSearchRes\",\"message\":{\"nodes\":[228],\"names\":[\"publiush\"],\"pics\":[\"\"]},\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T20:27:59.552Z\",\"ref\":\"2015-12-10T21:27:59.590+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));
    }

    @Test
    public void handleGroupInfoResponseTest() throws JSONException {
        String response = "{\"kind\":\"GroupInfoRes\",\"message\":{\"name\":\"Stack\",\"users\":[2],\"nodes\":[9],\"pic\":\"\",\"nid\":10},\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T23:18:34.696Z\",\"ref\":\"2015-12-11T00:18:33.668+01:00\"}}";
        mHandler.handleGroupInfoResponse(new Response(response));

    }

    @Test
    public void handleGroupMessageResponseTest() throws JSONException, ParseException {
        Message message = new Message(new Id(69), YieldsApplication.getUser().getId(), new TextContent
                ("topkek"), DateSerialization.dateSerializer.toDate("2015-12-10T23:48:22.679+01:00"));
        YieldsApplication.getUser().getGroup(new Id(10)).addMessage(message);
        String response = "{\"kind\":\"GroupMessageRes\",\"message\":{\"nid\":10,\"datetime\":\"2015-12-10T22:48:23.553Z\"},\"metadata\":{\"client\":1,\"datetime\":\"2015-12-10T22:48:23.554Z\",\"ref\":\"2015-12-10T23:48:22.679+01:00\"}}";
        mHandler.handleGroupMessageResponse(new Response(response));

    }

    @Test
    public void handlePublisherCreateResponseTest() throws JSONException, ParseException {
        Message message = new Message(new Id(69), YieldsApplication.getUser().getId(), new TextContent
                ("topkek"), DateSerialization.dateSerializer.toDate("2015-12-10T21:27:41.732+01:00"));
        YieldsApplication.getUser().getGroup(new Id(10)).addMessage(message);
        String response = "{\"kind\":\"PublisherCreateRes\",\"message\":{\"nid\":10},\"metadata\":{\"client\":1," +
                "\"datetime\":\"2015-12-10T20:27:41.692Z\",\"ref\":\"2015-12-10T21:27:41.732+01:00\"}}";
        mHandler.handlePublisherCreateResponse(new Response(response));

    }

    @Test
    public void handlePublisherUpdateResponseTest() throws JSONException {
        String response = "";
        //mHandler.handlePublisherUpdateBroadcast(new Response(response));

    }

    @Test
    public void handlePublisherInfoResponseTest() throws JSONException {
        String response = "{\"kind\":\"PublisherInfoRes\",\"message\":{\"name\":\"Toto\",\"tags\":[],\"users\":[],\"nodes\":[],\"pic\":\"\",\"nid\":258},\"metadata\":{\"client\":6,\"datetime\":\"2015-12-10T18:20:48.005Z\",\"ref\":\"2015-12-10T19:20:47.204+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handlePublisherMessageResponseTest() throws JSONException {
        String response = "{\"kind\":\"PublisherMessageRes\",\"message\":{\"nid\":228,\"datetime\":\"2015-12-10T20:31:52.079Z\"},\"metadata\":{\"client\":1,\"datetime\":\"2015-12-10T20:31:52.080Z\",\"ref\":\"2015-12-10T21:31:51.784+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleRSSCreateResponseTest() throws JSONException {
        String response = "{\"kind\":\"RSSCreateRes\",\"message\":{\"nid\":9},\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T23:17:22.044Z\",\"ref\":\"2015-12-11T00:17:20.749+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleUserUpdateBroadcastTest() throws JSONException {
        String response = "{\"kind\":\"UserUpdateBrd\",\"message\":{\"uid\":2,\"email\":\"ncasademont@gmail.com\",\"name\":\"Trofleb\",\"pic\":\"\"},\"metadata\":{\"client\":0,\"datetime\":\"2015-12-10T23:47:42.405Z\",\"ref\":\"-999999999-01-01T00:00+18:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleGroupCreateBroadcastTest() throws JSONException {
        String response = "{\"kind\":\"GroupCreateBrd\",\"message\":{\"nid\":37,\"name\":\"test\",\"users\":[2,3],\"nodes\":[]},\"metadata\":{\"client\":0,\"datetime\":\"2015-12-10T23:41:07.453Z\",\"ref\":\"-999999999-01-01T00:00+18:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleGroupUpdateBroadcastTest() throws JSONException {
        String response = "{\"kind\":\"GroupUpdateBrd\",\"message\":{\"name\":\"test\",\"users\":[2,3],\"nodes\":[],\"pic\":\"\",\"nid\":37},\"metadata\":{\"client\":0,\"datetime\":\"2015-12-10T23:41:57.693Z\",\"ref\":\"-999999999-01-01T00:00+18:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handlePublisherCreateBroadcastTest() throws JSONException {
        String response = "{\"kind\":\"PublisherCreateBrd\",\"message\":{\"nid\":41,\"name\":\"tewt\",\"users\":[2,3],\"nodes\":[]},\"metadata\":{\"client\":0,\"datetime\":\"2015-12-10T23:48:12.124Z\",\"ref\":\"-999999999-01-01T00:00+18:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handlePublisherUpdateBroadcastTest() throws JSONException {
        String response = "";
        //mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handlePublisherMessageBroadcastTest() throws JSONException {
        String response = "";
        //mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleRSSCreateBroadcastTest() throws JSONException {
        String response = "";
        //mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleRSSMessageBroadcastTest() throws JSONException {
        String response = "";
        //mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleGroupCreateResponseTest() throws JSONException {
        String response = "{\"kind\":\"GroupCreateRes\",\"message\":{\"nid\":10},\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T23:17:22.094Z\",\"ref\":\"2015-12-11T00:17:21.073+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleUserInfoResponseTest() throws JSONException {
        String response = "{\"kind\":\"UserInfoRes\",\"message\":{\"name\":\"test\",\"email\":\"teo@zifeo.com\",\"entourage\":[],\"entourageUpdatedAt\":[],\"uid\":3,\"pic\":\"\"},\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T23:27:43.269Z\",\"ref\":\"2015-12-11T00:27:42.220+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleUserGroupListResponseTest() throws JSONException {
        String response = "{\"kind\":\"UserNodeListRes\",\"message\":{\"groups\":[4,7],\"kinds\":[\"Group\",\"Group\"],\"updatedAt\":[\"2015-12-10T20:55:21.121Z\",\"2015-12-10T21:00:47.771Z\"],\"refreshedAt\":[\"2015-12-10T20:55:21.121Z\",\"2015-12-10T21:00:47.771Z\"]},\"metadata\":{\"client\":1,\"datetime\":\"2015-12-10T22:33:30.942Z\",\"ref\":\"2015-12-10T23:33:30.199+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleUserConnectResponseTest() throws JSONException {
        String response = "{\"kind\":\"UserConnectRes\",\"message\":{\"uid\":2,\"returning\":true},\"metadata\":{\"client\":0,\"datetime\":\"2015-12-10T23:28:14.147Z\",\"ref\":\"2015-12-11T00:28:13.137+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleNodeHistoryResponseTest() throws JSONException {
        String response = "{\"kind\":\"NodeHistoryRes\",\"message\":{\"texts\":[],\"datetimes\":[],\"contents\":[],\"senders\":[],\"contentTypes\":[],\"nid\":33,\"contentNids\":[]},\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T23:28:05.105Z\",\"ref\":\"2015-12-11T00:28:04.068+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleMediaMessageBroadcastTest() throws JSONException {
        String response = "";
        //mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleMediaMessageResponseTest() throws JSONException {
        String response = "{\"kind\":\"MediaMessageRes\",\"message\":{\"nid\":63,\"datetime\":\"2015-12-10T18:01:51.460Z\"},\"metadata\":{\"client\":1,\"datetime\":\"2015-12-10T18:01:51.461Z\",\"ref\":\"2015-12-10T19:01:51.412+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

    }

    @Test
    public void handleNodeMessageBroadcastTest() throws JSONException {
        String response = "{\"kind\":\"NodeMessageBrd\",\"message\":{\"nid\":6,\"datetime\":\"2015-12-10T23:39:34.013Z\",\"sender\":2,\"text\":\"hcjcjvcjcjcj\"},\"metadata\":{\"client\":0,\"datetime\":\"2015-12-10T23:39:34.019Z\",\"ref\":\"-999999999-01-01T00:00+18:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));

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
            mLastChange = change;
        }
    }

    private class MockCacheDatabaseHelper extends CacheDatabaseHelper{
        public Id mLastIdUpdated;
        public Group mLastGroupAdded;

        public Id mLastGroupIdImageChange;
        public Bitmap mLastImageChangeForGroup;

        public Id mLastGroupIdNameChanged;
        public String mLastGroupNameUpdated;

        public List<User> mLastUsersAdded;
        public Id mLastGroupReceivingNeUser;

        public List<User> mLastUsersRemoved;
        public Id mLastGroupRemovingNeUser;

        public User mLastUserAdded;

        public Id mLastUserIdNameUpdated;
        public String mLastNameChanged;

        public Message mLastMessageAdded;
        public Id mLastGroupIdMessageAdded;

        public MockCacheDatabaseHelper(){
            super();
        }

        @Override
        public void updateEntourage(Id userId, boolean inEntourage){
            mLastIdUpdated = userId;
        }

        @Override
        public void addGroup(Group group){
            mLastGroupAdded = group;
        }

        @Override
        public void updateGroupImage(Id groupId, Bitmap newGroupImage){
            mLastGroupIdImageChange = groupId;
            mLastImageChangeForGroup = newGroupImage;
        }

        @Override
        public void updateGroupName(Id groupId, String newGroupName){
            mLastGroupIdNameChanged = groupId;
            mLastGroupNameUpdated = newGroupName;
        }

        @Override
        public void addUsersToGroup(Id groupId, List<User> users){
            mLastUsersAdded = users;
            mLastGroupReceivingNeUser = groupId;
        }

        @Override
        public void removeUsersFromGroup(Id groupId, List<User> users){
            mLastUsersRemoved = users;
            mLastGroupRemovingNeUser = groupId;
        }

        @Override
        public void addUser(User user){
            mLastUserAdded = user;
        }

        @Override
        public void updateUserName(Id userId, String newUserName){
            mLastUserIdNameUpdated = userId;
            mLastNameChanged = newUserName;
        }

        @Override
        public void addMessage(Message message, Id groupId){
            mLastMessageAdded = message;
            mLastGroupIdMessageAdded = groupId;
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

    private void log(String message){
        Log.d("RequestHandlerTests", message);
    }
}
