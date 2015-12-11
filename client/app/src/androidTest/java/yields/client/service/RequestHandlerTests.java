package yields.client.service;


import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.SystemClock;
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
import yields.client.servicerequest.MediaMessageRequest;
import yields.client.servicerequest.NodeHistoryRequest;
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
public class RequestHandlerTests {
    private static MockYieldsService mService;
    private static MockCacheDatabaseHelper mCacheDatabaseHelper;
    private static MockServiceRequestController mServiceRequestController;
    private SQLiteDatabase mDatabase;

    private Message MOCK_RECEIVED_MESSAGE;


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
        ArrayList<Id> list = new ArrayList<>();
        list.add(YieldsApplication.getUser().getId());
        Group group = new Group("Nice meme", new Id(888), list);
        YieldsApplication.getUser().addGroup(group);
        mCacheDatabaseHelper.addGroup(group);
    }

    @Test
    public void testHandleUserGroupListRequest(){
        // Request
        UserGroupListRequest request = new UserGroupListRequest(YieldsApplication.getUser());
        // Handler
        RequestHandler handler = createRequestHandler();
        // Call the method to be tested.
        handler.handleUserGroupListRequest(request);
        // Testing the notifies
        assertEquals(NotifiableActivity.Change.GROUP_LIST, mService.mLastChange);
        // Testing the last request sent
        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            // Expected :
            // {"metadata":{"client":999999,"ref":<date>,"datetime":<date>},"kind":"UserNodeList","message":{}}
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals(metadata.getString("ref"), metadata.getString("datetime"));
            assertEquals("UserNodeList", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals(0, message.length());
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }

    @Test
    public void testHandleUserEntourageRemoveRequest(){
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
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals(metadata.getString("ref"), metadata.getString("datetime"));
            assertEquals("UserUpdate", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals(0, message.getJSONArray("addEntourage").length());
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
    public void testHandleUserInfoRequest(){
        // TODO : Long ass test.
    }

    @Test
    public void testGroupCreateRequest(){
        ArrayList<Id> users = new ArrayList<>();
        users.add(YieldsApplication.getUser().getId());
        Group group = new Group("mock group 420", new Id(420), users);
        // Request
        GroupCreateRequest request = new GroupCreateRequest(YieldsApplication.getUser(), group);
        // Handler
        RequestHandler handler = createRequestHandler();
        // Call the method to be tested.
        handler.handleGroupCreateRequest(request);
        // Testing the last request sent
        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals(metadata.getString("ref"), metadata.getString("datetime"));
            assertEquals("GroupCreate", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals(group.getName(), message.getString("name"));
            JSONArray nodes = message.getJSONArray("nodes");
            assertEquals(0 , nodes.length());
            JSONArray usersMessage = message.getJSONArray("users");
            assertEquals(1, usersMessage.length());
            assertEquals(999999, usersMessage.get(0));
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }

    @Test
    public void testHandleUserEntourageAddRequest(){
        UserEntourageAddRequest request = new UserEntourageAddRequest(YieldsApplication.getUser().getId(), new
                Id(117));

        RequestHandler handler = createRequestHandler();
        handler.handleUserEntourageAddRequest(request);

        assertEquals(Long.valueOf(117), mCacheDatabaseHelper.mLastIdUpdated.getId());

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        Log.d("RequestHandlerTests", sReq.message());
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals(metadata.getString("ref"), metadata.getString("datetime"));
            assertEquals("UserUpdate", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals(0, message.getJSONArray("removeEntourage").length());
            assertEquals("null", message.getString("pic"));
            assertEquals("null", message.getString("name"));
            assertEquals("null", message.getString("email"));
            JSONArray toRemove = message.getJSONArray("addEntourage");
            assertEquals(1, toRemove.length());
            assertEquals(117, toRemove.get(0));
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }

    @Test
    public void testHandleGroupUpdateImageRequest(){
        Bitmap newImage = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
        // Request
        GroupUpdateImageRequest request = new GroupUpdateImageRequest(YieldsApplication.getUser(), new Id(888),
                newImage, Group.GroupType.PRIVATE);
        // Handler
        RequestHandler handler = createRequestHandler();
        // Call the method to be tested.
        handler.handleGroupUpdateImageRequest(request);
        // Testing the changes in helper
        assertEquals(Long.valueOf(888), mCacheDatabaseHelper.mLastGroupIdImageChange.getId());
        assertTrue(newImage.sameAs(mCacheDatabaseHelper.mLastImageChangeForGroup));
        // Testing the last request sent
        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals("GroupUpdate", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals("null", message.getString("name"));
            assertEquals(0, message.getJSONArray("addNodes").length());
            assertEquals(0, message.getJSONArray("addUsers").length());
            assertEquals(0, message.getJSONArray("removeNodes").length());
            assertEquals(0, message.getJSONArray("removeUsers").length());
            assertEquals((long) 888, message.getLong("nid"));
            String imageBase64 = message.getString("pic");
            String expected = ImageSerialization.serializeImage(newImage, ImageSerialization.SIZE_IMAGE);
            assertEquals(expected, imageBase64);
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }

    @Test
    public void testHandleGroupUpdateNameRequest() {
        String newName = "TOP KEK";
        // Request
        GroupUpdateNameRequest request = new GroupUpdateNameRequest(YieldsApplication.getUser(), new Id(888),
                newName, Group.GroupType.PRIVATE);
        // Handler
        RequestHandler handler = createRequestHandler();
        // Call the method to be tested.
        handler.handleGroupUpdateNameRequest(request);
        // Testing the changes in helper
        assertEquals(Long.valueOf(888), mCacheDatabaseHelper.mLastGroupIdNameChanged.getId());
        assertEquals(newName, mCacheDatabaseHelper.mLastGroupNameUpdated);
        // Testing the last request sent
        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals("GroupUpdate", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals(0, message.getJSONArray("addNodes").length());
            assertEquals(0, message.getJSONArray("addUsers").length());
            assertEquals(0, message.getJSONArray("removeNodes").length());
            assertEquals(0, message.getJSONArray("removeUsers").length());
            assertEquals("TOP KEK", message.getString("name"));
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }

    @Test
    public void testHandleGroupUpdateUsersRequestAdd() {
        // Request
        ArrayList<User> userList = new ArrayList<>();
        userList.add(MockFactory.generateFakeUser("Tux", new Id(111), "topkek"));
        GroupUpdateUsersRequest request = new GroupUpdateUsersRequest(YieldsApplication.getUser().getId(), new Id
                (888), userList, GroupUpdateUsersRequest.UpdateType.ADD, Group.GroupType.PRIVATE);
        // Handler
        RequestHandler handler = createRequestHandler();
        // Call the method to be tested.
        handler.handleGroupUpdateUsersRequest(request);
        // Testing the changes in helper
        assertEquals(Long.valueOf(888), mCacheDatabaseHelper.mLastGroupReceivingNeUser.getId());
        assertEquals(1, mCacheDatabaseHelper.mLastUsersAdded.size());
        assertEquals(userList.get(0), mCacheDatabaseHelper.mLastUsersAdded.get(0));
        // Testing the last request sent
        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals("GroupUpdate", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals(0, message.getJSONArray("addNodes").length());
            assertEquals(1, message.getJSONArray("addUsers").length());
            assertEquals(0, message.getJSONArray("removeNodes").length());
            assertEquals(0, message.getJSONArray("removeUsers").length());
            assertEquals("null", message.getString("name"));
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }

    @Test
    public void testHandleGroupUpdateUsersRequestRemove() {
        // Request
        ArrayList<User> userList = new ArrayList<>();
        userList.add(MockFactory.generateFakeUser("Tux", new Id(111), "topkek"));
        GroupUpdateUsersRequest request = new GroupUpdateUsersRequest(YieldsApplication.getUser().getId(), new Id
                (888), userList, GroupUpdateUsersRequest.UpdateType.REMOVE, Group.GroupType.PRIVATE);
        // Handler
        RequestHandler handler = createRequestHandler();
        // Call the method to be tested.
        handler.handleGroupUpdateUsersRequest(request);
        // Testing the changes in helper
        assertEquals(Long.valueOf(888), mCacheDatabaseHelper.mLastGroupRemovingNeUser.getId());
        assertEquals(1, mCacheDatabaseHelper.mLastUsersRemoved.size());
        assertEquals(userList.get(0), mCacheDatabaseHelper.mLastUsersRemoved.get(0));
        // Testing the last request sent
        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals("GroupUpdate", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals(0, message.getJSONArray("addNodes").length());
            assertEquals(0, message.getJSONArray("addUsers").length());
            assertEquals(0, message.getJSONArray("removeNodes").length());
            assertEquals(1, message.getJSONArray("removeUsers").length());
            assertEquals("null", message.getString("name"));
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }

    @Test
    public void testHandleUserUpdateRequest() {
        User user = new User("NEW NAME", new Id(11111), "NEW EMAIL", Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));
        UserUpdateRequest request = new UserUpdateRequest(user);

        RequestHandler handler = createRequestHandler();
        handler.handleUserUpdateRequest(request);

        assertEquals(user.getId(), mCacheDatabaseHelper.mLastUserAdded.getId());

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(11111, metadata.getLong("client"));
            assertEquals(metadata.getString("ref"), metadata.getString("datetime"));
            assertEquals("UserUpdate", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals("NEW NAME", message.getString("name"));
            assertEquals("NEW EMAIL", message.getString("email"));
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }

    @Test
    public void testHandleUserUpdateNameRequest() {
        String newName = "Holy cow !";
        User user = new User(newName, new Id(11111), "NEW EMAIL", Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));

        UserUpdateNameRequest request = new UserUpdateNameRequest(user);

        RequestHandler handler = createRequestHandler();
        handler.handleUserUpdateNameRequest(request);

        assertEquals(user.getId().getId(), mCacheDatabaseHelper.mLastUserIdNameUpdated.getId());

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(11111, metadata.getLong("client"));
            assertEquals(metadata.getString("ref"), metadata.getString("datetime"));
            assertEquals("UserUpdate", reqObject.getString("kind"));
            JSONObject message = reqObject.getJSONObject("message");
            assertEquals("Holy cow !", message.getString("name"));
            assertEquals("null", message.getString("email"));
            assertEquals("null", message.getString("pic"));
            assertEquals(0, message.getJSONArray("addEntourage").length());
            assertEquals(0, message.getJSONArray("removeEntourage").length());
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }

    @Test
    public void testHandleNodeMessageRequestText() {
        String newName = "Holy cow !";
        User user = new User(newName, new Id(11111), "NEW EMAIL", Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));

        Date sendingDate = new Date();
        Message message = new Message(new Id(90), YieldsApplication.getUser().getId(), new TextContent
                ("topkek"), sendingDate);

        GroupMessageRequest request = new GroupMessageRequest(message, new Id(888), Group.GroupType.PRIVATE);

        RequestHandler handler = createRequestHandler();
        handler.handleNodeMessageRequest(request);

        assertEquals(Long.valueOf(888), mCacheDatabaseHelper.mLastGroupIdMessageAdded.getId());
        assertEquals(message, mCacheDatabaseHelper.mLastMessageAdded);
        assertEquals(sendingDate, YieldsApplication.getUser().getGroup(new Id(888)).getLastUpdate());

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals(metadata.getString("ref"), metadata.getString("datetime"));
            assertEquals("GroupMessage", reqObject.getString("kind"));
            JSONObject messageObj = reqObject.getJSONObject("message");
            assertEquals("null", messageObj.getString("contentType"));
            assertEquals(888, messageObj.getLong("nid"));
            assertEquals(sendingDate, DateSerialization.dateSerializer.toDate(messageObj.getString("date")));
            assertEquals("topkek", messageObj.getString("text"));
            assertEquals("null", messageObj.getString("content"));
        } catch (JSONException e) {
            fail("Invalid request");
        } catch (ParseException e) {
            fail("cannot parse date.");
        }
    }

    @Test
    public void testHandleNodeMessageRequestURL() {
        String newName = "Holy cow !";
        User user = new User(newName, new Id(11111), "NEW EMAIL", Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));

        Date sendingDate = new Date();
        Message message = new Message(new Id(90), YieldsApplication.getUser().getId(), new UrlContent
                ("topkek 4chan.org"), sendingDate);

        GroupMessageRequest request = new GroupMessageRequest(message, new Id(888), Group.GroupType.PRIVATE);

        RequestHandler handler = createRequestHandler();
        handler.handleNodeMessageRequest(request);

        assertEquals(Long.valueOf(888), mCacheDatabaseHelper.mLastGroupIdMessageAdded.getId());
        assertEquals(message, mCacheDatabaseHelper.mLastMessageAdded);
        assertEquals(sendingDate, YieldsApplication.getUser().getGroup(new Id(888)).getLastUpdate());

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals("GroupMessage", reqObject.getString("kind"));
            JSONObject messageObj = reqObject.getJSONObject("message");
            assertEquals("url", messageObj.getString("contentType"));
            assertEquals(888, messageObj.getLong("nid"));
            assertEquals(sendingDate, DateSerialization.dateSerializer.toDate(messageObj.getString("date")));
            assertEquals("topkek 4chan.org", messageObj.getString("text"));
            assertEquals("https://www.4chan.org", messageObj.getString("content"));
        } catch (JSONException e) {
            fail("Invalid request");
        } catch (ParseException e) {
            fail("cannot parse date.");
        }
    }

    @Test
    public void testHandleNodeMessageRequestImage() {
        String newName = "Holy cow !";
        User user = new User(newName, new Id(11111), "NEW EMAIL", Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));

        Date sendingDate = new Date();
        ImageContent content = new ImageContent(Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565), "Nice caption " +
                "m8.");
        Message message = new Message(new Id(90), YieldsApplication.getUser().getId(), content, sendingDate);

        GroupMessageRequest request = new GroupMessageRequest(message, new Id(888), Group.GroupType.PRIVATE);

        RequestHandler handler = createRequestHandler();
        handler.handleNodeMessageRequest(request);

        assertEquals(Long.valueOf(888), mCacheDatabaseHelper.mLastGroupIdMessageAdded.getId());
        assertEquals(message, mCacheDatabaseHelper.mLastMessageAdded);
        assertEquals(sendingDate, YieldsApplication.getUser().getGroup(new Id(888)).getLastUpdate());

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals("GroupMessage", reqObject.getString("kind"));
            JSONObject messageObj = reqObject.getJSONObject("message");
            assertEquals("image", messageObj.getString("contentType"));
            assertEquals(888, messageObj.getLong("nid"));
            assertEquals(sendingDate, DateSerialization.dateSerializer.toDate(messageObj.getString("date")));
            assertEquals("Nice caption m8.", messageObj.getString("text"));
            String contentField = messageObj.getString("content");
            String expectedContentField = ImageSerialization.serializeImage(Bitmap.createBitmap(100, 100, Bitmap
                    .Config.RGB_565), ImageSerialization.SIZE_IMAGE_NODE);
            assertEquals(expectedContentField, contentField);
        } catch (JSONException e) {
            fail("Invalid request");
        } catch (ParseException e) {
            fail("cannot parse date.");
        }
    }

    @Test
    public void testHandleNodeHistoryRequest(){
        Date sendingDate = new Date();
        MOCK_RECEIVED_MESSAGE = new Message(new Id(90), YieldsApplication.getUser().getId(), new
                TextContent("top kek"), new Date());
        SystemClock.sleep(2000);
        NodeHistoryRequest request = new NodeHistoryRequest(new Id(888), new Date());

        RequestHandler handler = createRequestHandler();
        handler.handleNodeHistoryRequest(request);

        assertEquals(Long.valueOf(888), mService.mLastReceivingNodeId.getId());
        assertEquals(MOCK_RECEIVED_MESSAGE, mService.mLastReceivedMessages.get(0));

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals("NodeHistory", reqObject.getString("kind"));
            JSONObject messageObj = reqObject.getJSONObject("message");
            assertEquals(888, messageObj.getLong("nid"));
            assertEquals(10, messageObj.getInt("count"));
        } catch (JSONException e) {
            fail("Invalid request");
        }
    }

    @Test
    public void testHandleMediaMessageRequestText() {
        Date sendingDate = new Date();
        MOCK_RECEIVED_MESSAGE = new Message(new Id(90), YieldsApplication.getUser().getId(), new
                TextContent("top kek"), sendingDate);
        SystemClock.sleep(2000);
        MediaMessageRequest request = new MediaMessageRequest(MOCK_RECEIVED_MESSAGE, new Id(888));

        RequestHandler handler = createRequestHandler();
        handler.handleMediaMessageRequest(request);

        assertEquals(MOCK_RECEIVED_MESSAGE, mCacheDatabaseHelper.mLastMessageAdded);
        assertEquals(new Id(888), mCacheDatabaseHelper.mLastGroupIdMessageAdded);

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals("MediaMessage", reqObject.getString("kind"));
            JSONObject messageObj = reqObject.getJSONObject("message");
            assertEquals("null", messageObj.getString("contentType"));
            assertEquals(888, messageObj.getLong("nid"));
            assertEquals(sendingDate, DateSerialization.dateSerializer.toDate(messageObj.getString("date")));
            assertEquals("top kek", messageObj.getString("text"));
            assertEquals("null", messageObj.getString("content"));
        } catch (JSONException e) {
            fail("Invalid request");
        } catch (ParseException e) {
            fail("cannot parse date.");
        }
    }

    @Test
    public void testHandleMediaMessageRequestURL() {
        Date sendingDate = new Date();
        MOCK_RECEIVED_MESSAGE = new Message(new Id(90), YieldsApplication.getUser().getId(), new UrlContent("topkek 4chan.org"),
                sendingDate);
        SystemClock.sleep(2000);
        MediaMessageRequest request = new MediaMessageRequest(MOCK_RECEIVED_MESSAGE, new Id(888));

        RequestHandler handler = createRequestHandler();
        handler.handleMediaMessageRequest(request);

        assertEquals(MOCK_RECEIVED_MESSAGE, mCacheDatabaseHelper.mLastMessageAdded);
        assertEquals(new Id(888), mCacheDatabaseHelper.mLastGroupIdMessageAdded);

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals("MediaMessage", reqObject.getString("kind"));
            JSONObject messageObj = reqObject.getJSONObject("message");
            assertEquals("url", messageObj.getString("contentType"));
            assertEquals(888, messageObj.getLong("nid"));
            assertEquals(sendingDate, DateSerialization.dateSerializer.toDate(messageObj.getString("date")));
            assertEquals("topkek 4chan.org", messageObj.getString("text"));
            assertEquals("https://www.4chan.org", messageObj.getString("content"));
        } catch (JSONException e) {
            fail("Invalid request");
        } catch (ParseException e) {
            fail("cannot parse date.");
        }
    }

    @Test
    public void testHandleMediaMessageRequestImage() {
        Date sendingDate = new Date();
        ImageContent content = new ImageContent(Bitmap.createBitmap(100, 100, Bitmap
                .Config.RGB_565), "Nice caption m8.");
        MOCK_RECEIVED_MESSAGE = new Message(new Id(90), YieldsApplication.getUser().getId(),content, sendingDate);
        SystemClock.sleep(2000);
        MediaMessageRequest request = new MediaMessageRequest(MOCK_RECEIVED_MESSAGE, new Id(888));

        RequestHandler handler = createRequestHandler();
        handler.handleMediaMessageRequest(request);

        assertEquals(MOCK_RECEIVED_MESSAGE, mCacheDatabaseHelper.mLastMessageAdded);
        assertEquals(new Id(888), mCacheDatabaseHelper.mLastGroupIdMessageAdded);

        ServerRequest sReq = mServiceRequestController.mLastRequest;
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(sReq.message());
            JSONObject metadata = reqObject.getJSONObject("metadata");
            assertEquals(999999, metadata.getLong("client"));
            assertEquals("MediaMessage", reqObject.getString("kind"));
            JSONObject messageObj = reqObject.getJSONObject("message");
            assertEquals("image", messageObj.getString("contentType"));
            assertEquals(888, messageObj.getLong("nid"));
            assertEquals(sendingDate, DateSerialization.dateSerializer.toDate(messageObj.getString("date")));
            assertEquals("Nice caption m8.", messageObj.getString("text"));
            String contentField = messageObj.getString("content");
            String expectedContentField = ImageSerialization.serializeImage(Bitmap.createBitmap(100, 100, Bitmap
                    .Config.RGB_565), ImageSerialization.SIZE_IMAGE_NODE);
            assertEquals(expectedContentField, contentField);
        } catch (JSONException e) {
            fail("Invalid request");
        } catch (ParseException e) {
            fail("cannot parse date.");
        }
    }

    private RequestHandler createRequestHandler(){
        return new RequestHandler(mCacheDatabaseHelper, mService, mServiceRequestController);
    }

    private class MockYieldsService extends YieldService{
        public NotifiableActivity.Change mLastChange;
        public List<Message> mLastReceivedMessages;
        public Id mLastReceivingNodeId;

        public MockYieldsService(){
            super();
        }

        @Override
        public void notifyChange(NotifiableActivity.Change change){
            //super.notifyChange(change);
            mLastChange = change;
        }

        @Override
        synchronized public void receiveMessages(Id groupId, List<Message> messages){
            mLastReceivedMessages = messages;
            mLastReceivingNodeId = groupId;
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
            super.addGroup(group);
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

        @Override
        public List<Message> getMessagesForGroup(Id nodeId, Date furthestDate, int messageCount){
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(MOCK_RECEIVED_MESSAGE);
            return messages;
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
