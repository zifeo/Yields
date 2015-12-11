package yields.client.service;


import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yields.client.activities.MockFactory;
import yields.client.activities.NotifiableActivity;
import yields.client.cache.CacheDatabaseHelper;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;

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
    public void handleUserSearchResponseTest() throws JSONException {
        String response = "{\"kind\":\"UserSearchRes\",\"message\":{\"uid\":1},\"metadata\":{\"client\":3," +
                "\"datetime\":\"2015-12-10T23:58:28.798Z\",\"ref\":\"2015-12-11T00:58:28.745+01:00\"}}";
        mHandler.handleUserSearchResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.ADD_ENTOURAGE, mService.mLastChange);
    }


    @Test
    public void handleNodeSearchResponseTest() throws JSONException {
        String response = "{\"kind\":\"NodeSearchRes\",\"message\":{\"nodes\":[228],\"names\":[\"publiush\"" +
                "],\"pics\":[\"\"]},\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T20:27:59.552Z\",\"" +
                "ref\":\"2015-12-10T21:27:59.590+01:00\"}}";
        mHandler.handleNodeSearchResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.GROUP_SEARCH, mService.mLastChange);
        assertEquals(1, YieldsApplication.getGroupsSearched().size());
    }

    @Test
    public void handleGroupInfoResponseTest() throws JSONException {
        String response = "{\"kind\":\"GroupInfoRes\",\"message\":{\"name\":\"Stack\",\"users\":[2],\"nodes\"" +
                ":[9],\"pic\":\"\",\"nid\":10},\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T23:18:34.696Z\"" +
                ",\"ref\":\"2015-12-11T00:18:33.668+01:00\"}}";
        mHandler.handleGroupInfoResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.GROUP_LIST, mService.mLastChange);
        assertEquals(1, YieldsApplication.getGroupsSearched().size());
        assertEquals("Stack", mCacheDatabaseHelper.mLastGroupAdded.getName());
        assertEquals("2", mCacheDatabaseHelper.mLastGroupAdded.getUsers().get(0).getId().getId().toString());
        assertEquals("9", mCacheDatabaseHelper.mLastGroupAdded.getNodes().get(0).getId().getId().toString());
        assertEquals("10", mCacheDatabaseHelper.mLastGroupAdded.getId().getId().toString());
    }

    @Test
    public void handleGroupMessageResponseTest() throws JSONException, ParseException {
        Message message = new Message(new Id(69), YieldsApplication.getUser().getId(), new TextContent
                ("topkek"), DateSerialization.dateSerializer.toDate("2015-12-10T23:48:22.679+01:00"));
        YieldsApplication.getUser().getGroup(new Id(10)).addMessage(message);
        String response = "{\"kind\":\"GroupMessageRes\",\"message\":{\"nid\":10,\"datetime\":\"" +
                "2015-12-10T22:48:23.553Z\"},\"metadata\":{\"client\":1,\"datetime\":\"2015-12-10T22:48:23.554Z\"" +
                ",\"ref\":\"2015-12-10T23:48:22.679+01:00\"}}";
        mHandler.handleGroupMessageResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.MESSAGES_RECEIVE, mService.mLastChange);
        assertEquals(YieldsApplication.getUser().getId().getId().toString(),
                mCacheDatabaseHelper.mLastMessageAdded.getSender().getId().toString());
    }

    @Test
    public void handlePublisherCreateResponseTest() throws JSONException, ParseException {
        Message message = new Message(new Id(69), YieldsApplication.getUser().getId(), new TextContent
                ("topkek"), DateSerialization.dateSerializer.toDate("2015-12-10T21:27:41.732+01:00"));
        YieldsApplication.getUser().getGroup(new Id(10)).addMessage(message);
        YieldsApplication.getUser().getGroup(new Id(10)).setRef(DateSerialization.dateSerializer.toDate("2015-12-10T21:27:41.732+01:00"));
        String response = "{\"kind\":\"PublisherCreateRes\",\"message\":{\"nid\":10},\"metadata\":{\"client\":1," +
                "\"datetime\":\"2015-12-10T20:27:41.692Z\",\"ref\":\"2015-12-10T21:27:41.732+01:00\"}}";
        mHandler.handlePublisherCreateResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.MESSAGES_RECEIVE, mService.mLastChange);
        assertEquals("10", mCacheDatabaseHelper.mLastGroupAdded.getId().getId().toString());
    }

    @Test
    public void handlePublisherUpdateResponseTest() throws JSONException {
        String response = "";
        //mHandler.handlePublisherUpdateBroadcast(new Response(response));

    }

    @Test
    public void handlePublisherInfoResponseTest() throws JSONException {
        String response = "{\"kind\":\"PublisherInfoRes\",\"message\":{\"name\":\"Toto\",\"tags\":[],\"" +
                "users\":[2],\"nodes\":[3],\"pic\":\"\",\"nid\":258},\"metadata\":{\"client\":6,\"datetime\"" +
                ":\"2015-12-10T18:20:48.005Z\",\"ref\":\"2015-12-10T19:20:47.204+01:00\"}}";
        mHandler.handlePublisherInfoResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.GROUP_LIST, mService.mLastChange);
        assertEquals("Toto", mCacheDatabaseHelper.mLastGroupAdded.getName());
        assertEquals("2", mCacheDatabaseHelper.mLastGroupAdded.getUsers().get(0).getId().getId().toString());
        assertEquals("3", mCacheDatabaseHelper.mLastGroupAdded.getNodes().get(0).getId().getId().toString());
        assertEquals("258", mCacheDatabaseHelper.mLastGroupAdded.getId().getId().toString());
    }

    @Test
    public void handlePublisherMessageResponseTest() throws JSONException, ParseException {
        String response = "{\"kind\":\"PublisherMessageRes\",\"message\":{\"nid\":999,\"datetime\":" +
                "\"2015-12-10T20:31:52.079Z\"},\"metadata\":{\"client\":1,\"datetime\":\"2015-12-10T20:31:52.080Z\"" +
                ",\"ref\":\"2015-12-10T21:31:51.784+01:00\"}}";
        User user2 = new User("Johny", new Id(1), "aksdf@gmail.com", YieldsApplication.getDefaultUserImage());
        YieldsApplication.getUser().addUserToEntourage(user2);
        Group node3 = MockFactory.generateMockGroups(2).get(1);
        node3.setId(new Id(999));
        YieldsApplication.getUser().addGroup(node3);

        Message message = new Message(new Id(-1), user2.getId(), MockFactory.generateFakeTextContent(2),
                DateSerialization.dateSerializer.toDate("2015-12-10T21:31:51.784+01:00"), Message.MessageStatus.NOT_SENT);
        node3.addMessage(message);
        mHandler.handlePublisherMessageResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.MESSAGES_RECEIVE, mService.mLastChange);
        assertEquals(DateSerialization.dateSerializer.toDate("2015-12-10T20:31:52.079Z"),
                mCacheDatabaseHelper.mLastMessageAdded.getDate());
    }

    @Test
    public void handleRSSCreateResponseTest() throws JSONException, ParseException {
        String response = "{\"kind\":\"RSSCreateRes\",\"message\":{\"nid\":999},\"metadata\":{\"client\"" +
                ":2,\"datetime\":\"2015-12-10T23:17:22.044Z\",\"ref\":\"2015-12-11T00:17:20.749+01:00\"}}";
        User user2 = new User("Johny", new Id(2), "aksdf@gmail.com", YieldsApplication.getDefaultUserImage());
        YieldsApplication.getUser().addUserToEntourage(user2);
        Group node3 = MockFactory.generateMockGroups(2).get(1);
        node3.setId(new Id(999));
        node3.setRef(DateSerialization.dateSerializer.toDate("2015-12-11T00:17:20.749+01:00"));
        YieldsApplication.getUser().addGroup(node3);
        YieldsApplication.getUser().addNode(node3);

        mHandler.handleRSSCreateResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.RSS_CREATE, mService.mLastChange);
    }

    @Test
    public void handleUserUpdateBroadcastTest() throws JSONException {
        String response = "{\"kind\":\"UserUpdateBrd\",\"message\":{\"uid\":2,\"email\":" +
                "\"ncmont@gmail.com\",\"name\":\"Trofleb\",\"pic\":\"\"},\"metadata\":{\"client\":0," +
                "\"datetime\":\"2015-12-10T23:47:42.405Z\",\"ref\":\"-999999999-01-01T00:00+18:00\"}}";
        mHandler.handleUserUpdateBroadcast(new Response(response));
        assertEquals(NotifiableActivity.Change.ENTOURAGE_UPDATE, mService.mLastChange);
        assertEquals("Trofleb", mCacheDatabaseHelper.mLastUserAdded.getName());
        assertEquals("ncmont@gmail.com", mCacheDatabaseHelper.mLastUserAdded.getEmail());
        assertEquals("2", mCacheDatabaseHelper.mLastUserAdded.getId().getId().toString());
    }

    @Test
    public void handleGroupCreateBroadcastTest() throws JSONException {
        String response = "{\"kind\":\"GroupCreateBrd\",\"message\":{\"nid\":10,\"name\":\"test\"," +
                "\"users\":[2,4],\"nodes\":[3]},\"metadata\":{\"client\":0,\"datetime\":" +
                "\"2015-12-10T23:41:07.453Z\",\"ref\":\"-999999999-01-01T00:00+18:00\"}}";
        User user2 = new User("Johny", new Id(2), "aksdf@gmail.com", YieldsApplication.getDefaultUserImage());
        YieldsApplication.getUser().addUserToEntourage(user2);
        Group node3 = MockFactory.generateMockGroups(2).get(1);
        node3.setId(new Id(3));
        YieldsApplication.getUser().addNode(node3);

        mHandler.handleGroupCreateBroadcast(new Response(response));
        assertEquals(NotifiableActivity.Change.GROUP_LIST, mService.mLastChange);
        assertEquals("test", mCacheDatabaseHelper.mLastGroupAdded.getName());
        assertEquals("2", mCacheDatabaseHelper.mLastGroupAdded.getUsers().get(0).getId().getId().toString());
        assertEquals("3", mCacheDatabaseHelper.mLastGroupAdded.getNodes().get(0).getId().getId().toString());
        assertEquals("10", mCacheDatabaseHelper.mLastGroupAdded.getId().getId().toString());
    }

    @Test
    public void handleGroupUpdateBroadcastTest() throws JSONException {
        String response = "{\"kind\":\"GroupUpdateBrd\",\"message\":{\"name\":\"test\",\"users\":[2,3]," +
                "\"nodes\":[],\"pic\":\"\",\"nid\":37},\"metadata\":{\"client\":0,\"datetime\":" +
                "\"2015-12-10T23:41:57.693Z\",\"ref\":\"-999999999-01-01T00:00+18:00\"}}";
        mHandler.handleGroupUpdateBroadcast(new Response(response));
    }

    @Test
    public void handlePublisherCreateBroadcastTest() throws JSONException {
        String response = "{\"kind\":\"PublisherCreateBrd\",\"message\":{\"nid\":10,\"name\":\"tewt\"" +
                ",\"users\":[2],\"nodes\":[3,4]},\"metadata\":{\"client\":0,\"datetime\":" +
                "\"2015-12-10T23:48:12.124Z\",\"ref\":\"-999999999-01-01T00:00+18:00\"}}";

        User user2 = new User("Johny", new Id(2), "aksdf@gmail.com", YieldsApplication.getDefaultUserImage());
        YieldsApplication.getUser().addUserToEntourage(user2);
        Group node3 = MockFactory.generateMockGroups(2).get(1);
        node3.setId(new Id(3));
        YieldsApplication.getUser().addNode(node3);

        mHandler.handlePublisherCreateBroadcast(new Response(response));
        assertEquals(NotifiableActivity.Change.GROUP_LIST, mService.mLastChange);
        assertEquals("tewt", mCacheDatabaseHelper.mLastGroupAdded.getName());
        assertEquals("2", mCacheDatabaseHelper.mLastGroupAdded.getUsers().get(0).getId().getId().toString());
        assertEquals("3", mCacheDatabaseHelper.mLastGroupAdded.getNodes().get(0).getId().getId().toString());
        assertEquals("10", mCacheDatabaseHelper.mLastGroupAdded.getId().getId().toString());
    }

    @Test
    public void handlePublisherUpdateBroadcastTest() throws JSONException {
        String response = "";
        //mHandler.handlePublisherUpdateBroadcast(new Response(response));

    }

    @Test
    public void handlePublisherMessageBroadcastTest() throws JSONException {
        String response = "";
        //mHandler.handlePublisherMessageBroadcast(new Response(response));

    }

    @Test
    public void handleRSSCreateBroadcastTest() throws JSONException {
        String response = "";
        //mHandler.handleRSSCreateBroadcast(new Response(response));

    }

    @Test
    public void handleRSSMessageBroadcastTest() throws JSONException {
        String response = "";
        //mHandler.handleRSSMessageBroadcast(new Response(response));

    }

    @Test
    public void handleGroupCreateResponseTest() throws JSONException, ParseException {
        String response = "{\"kind\":\"GroupCreateRes\",\"message\":{\"nid\":999},\"metadata\":{" +
                "\"client\":10,\"datetime\":\"2015-12-10T22:48:23.553Z\",\"ref\":\"2015-12-10T22:48:23.553Z\"}}";
        User user2 = new User("Johny", new Id(1), "aksdf@gmail.com", YieldsApplication.getDefaultUserImage());
        YieldsApplication.getUser().addUserToEntourage(user2);
        Group node3 = MockFactory.generateMockGroups(2).get(1);
        node3.setId(new Id(999));
        node3.setRef(DateSerialization.dateSerializer.toDate("2015-12-10T22:48:23.553Z"));
        YieldsApplication.getUser().addGroup(node3);

        mHandler.handleGroupCreateResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.GROUP_LIST, mService.mLastChange);
        assertEquals("999", mCacheDatabaseHelper.mLastGroupAdded.getId().getId().toString());
    }

    @Test
    public void handleUserInfoResponseTest() throws JSONException {
        String response = "{\"kind\":\"UserInfoRes\",\"message\":{\"name\":\"test\",\"email\":" +
                "\"dsf@zifeo.com\",\"entourage\":[],\"entourageUpdatedAt\":[],\"uid\":3,\"pic\":\"" +
                "\"},\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T23:27:43.269Z\",\"ref\":" +
                "\"2015-12-11T00:27:42.220+01:00\"}}";
        mHandler.handleUserInfoResponse(new Response(response));
        assertEquals("test", mCacheDatabaseHelper.mLastUserAdded.getName());
        assertEquals("dsf@zifeo.com", mCacheDatabaseHelper.mLastUserAdded.getEmail());
        assertEquals("3", mCacheDatabaseHelper.mLastUserAdded.getId().getId().toString());
    }

    @Test
    public void handleUserInfoResponseTest2() throws JSONException {
        String response = "{\"kind\":\"UserInfoRes\",\"message\":{\"name\":\"test\",\"email\":" +
                "\"dsf@zifeo.com\",\"entourage\":[],\"entourageUpdatedAt\":[],\"uid\":10,\"pic\":\"" +
                "\"},\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T23:27:43.269Z\",\"ref\":" +
                "\"2015-12-11T00:27:42.220+01:00\"}}";
        mHandler.handleUserInfoResponse(new Response(response));
        assertEquals("test", mCacheDatabaseHelper.mLastUserAdded.getName());
        assertEquals("dsf@zifeo.com", mCacheDatabaseHelper.mLastUserAdded.getEmail());
        assertEquals("10", mCacheDatabaseHelper.mLastUserAdded.getId().getId().toString());
    }

    @Test
    public void handleUserGroupListResponseTest() throws JSONException {
        String response = "{\"kind\":\"UserNodeListRes\",\"message\":{\"groups\":[4,7],\"kinds\":[\"" +
                "Group\",\"Group\"],\"updatedAt\":[\"2015-12-10T20:55:21.121Z\",\"2015-12-10T21:00:47.771Z\"" +
                "],\"refreshedAt\":[\"2015-12-10T20:55:21.121Z\",\"2015-12-10T21:00:47.771Z\"]},\"metadata\"" +
                ":{\"client\":1,\"datetime\":\"2015-12-10T22:33:30.942Z\",\"ref\":\"2015-12-10T23:33:30.199+01:00\"}}";
        mHandler.handleUserNodeListResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.GROUP_LIST, mService.mLastChange);
    }

    @Test
    public void handleUserConnectResponseTest() throws JSONException {
        String response = "{\"kind\":\"UserConnectRes\",\"message\":{\"uid\":2,\"returning\":true}," +
                "\"metadata\":{\"client\":0,\"datetime\":\"2015-12-10T23:28:14.147Z\",\"ref\":" +
                "\"2015-12-11T00:28:13.137+01:00\"}}";
        mHandler.handleUserConnectResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.NEW_USER, mService.mLastChange);
    }

    @Test
    public void handleNodeHistoryResponseTest() throws JSONException {
        String response = "{\"kind\":\"NodeHistoryRes\",\"message\":{\"texts\":[],\"datetimes\":[]," +
                "\"contents\":[],\"senders\":[],\"contentTypes\":[],\"nid\":33,\"contentNids\":[]}," +
                "\"metadata\":{\"client\":2,\"datetime\":\"2015-12-10T23:28:05.105Z\",\"ref\":" +
                "\"2015-12-11T00:28:04.068+01:00\"}}";
        mHandler.handleNodeHistoryResponse(new Response(response));
    }

    @Test
    public void handleMediaMessageBroadcastTest() throws JSONException {
        String response = "";
        //mHandler.handleMediaMessageBroadcast(new Response(response));

    }

    @Test
    public void handleMediaMessageResponseTest() throws JSONException, ParseException {
        String response = "{\"kind\":\"MediaMessageRes\",\"message\":{\"nid\":10,\"datetime\":" +
                "\"2015-12-10T18:01:51.460Z\"},\"metadata\":{\"client\":1,\"datetime\":" +
                "\"2015-12-10T18:01:51.461Z\",\"ref\":\"2015-12-10T19:01:51.412+01:00\"}}";
        mHandler.handleMediaMessageResponse(new Response(response));
        assertEquals(NotifiableActivity.Change.MESSAGES_RECEIVE, mService.mLastChange);
        assertEquals(DateSerialization.dateSerializer.toDate("2015-12-10T18:01:51.460Z"),
                mCacheDatabaseHelper.mLastMessageAdded.getDate());
    }

    @Test
    public void handleNodeMessageBroadcastTest() throws JSONException, ParseException {
        String response = "{\"kind\":\"NodeMessageBrd\",\"message\":{\"nid\":10,\"datetime\":" +
                "\"2015-12-10T23:39:34.013Z\",\"sender\":2,\"text\":\"hcjcjvcjcjcj\"},\"metadata\"" +
                ":{\"client\":0,\"datetime\":\"2015-12-10T23:39:34.019Z\",\"ref\":\"-999999999-01-01T00:00+18:00\"}}";
        mHandler.handleNodeMessageBroadcast(new Response(response));
        assertEquals(DateSerialization.dateSerializer.toDate("2015-12-10T23:39:34.013Z"),
                mCacheDatabaseHelper.mLastMessageAdded.getDate());
    }

    private class MockYieldsService extends YieldService {

        public NotifiableActivity.Change mLastChange;

        public MockYieldsService() {
            super();
        }

        @Override
        public void notifyChange(NotifiableActivity.Change change) {
            //super.notifyChange(change);
            mLastChange = change;
        }

    }

    private class MockCacheDatabaseHelper extends CacheDatabaseHelper {

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

        public MockCacheDatabaseHelper() {
            super();
        }

        @Override
        public void updateEntourage(Id userId, boolean inEntourage) {
            mLastIdUpdated = userId;
        }

        @Override
        public void addGroup(Group group) {
            mLastGroupAdded = group;
        }

        @Override
        public void updateGroupImage(Id groupId, Bitmap newGroupImage) {
            mLastGroupIdImageChange = groupId;
            mLastImageChangeForGroup = newGroupImage;
        }

        @Override
        public void updateGroupName(Id groupId, String newGroupName) {
            mLastGroupIdNameChanged = groupId;
            mLastGroupNameUpdated = newGroupName;
        }

        @Override
        public void addUsersToGroup(Id groupId, List<User> users) {
            mLastUsersAdded = users;
            mLastGroupReceivingNeUser = groupId;
        }

        @Override
        public void removeUsersFromGroup(Id groupId, List<User> users) {
            mLastUsersRemoved = users;
            mLastGroupRemovingNeUser = groupId;
        }

        @Override
        public void addUser(User user) {
            mLastUserAdded = user;
        }

        @Override
        public void updateUserName(Id userId, String newUserName) {
            mLastUserIdNameUpdated = userId;
            mLastNameChanged = newUserName;
        }

        @Override
        public void addMessage(Message message, Id groupId) {
            mLastMessageAdded = message;
            mLastGroupIdMessageAdded = groupId;
        }

    }

    private class MockServiceRequestController extends ServiceRequestController {

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
