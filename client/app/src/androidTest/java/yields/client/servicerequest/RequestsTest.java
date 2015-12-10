package yields.client.servicerequest;

import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yields.client.generalhelpers.MockModel;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static yields.client.activities.MockFactory.generateFakeClientUser;
import static yields.client.activities.MockFactory.generateFakeTextContent;
import static yields.client.activities.MockFactory.generateFakeUser;
import static yields.client.activities.MockFactory.generateMockGroups;
import static yields.client.activities.MockFactory.generateMockImage;
import static yields.client.activities.MockFactory.generateMockMessage;

public class RequestsTest {

    private final User mUser;
    private final Group mGroup;
    private final Message mMessage;
    private final Bitmap mImage;
    private final ClientUser mClientUser;

    public RequestsTest() {
        new MockModel();
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getTargetContext());
        mUser = generateFakeUser("name", new Id(-1), "test@yields.im");
        mGroup = generateMockGroups(1).get(0);
        mGroup.addUser(mUser.getId());
        mGroup.addNode(mGroup);
        mMessage = generateMockMessage("group", mGroup.getId(), mUser, generateFakeTextContent(0));
        mImage = generateMockImage();
        mClientUser = generateFakeClientUser("name", new Id(-1), "test@yields.im", mImage);
    }

    @Test
    public void groupCreateRequestTest() {

        GroupCreateRequest req = new GroupCreateRequest(mUser, mGroup);
        assertEquals(mGroup, req.getGroup());
        assertEquals(ServiceRequest.RequestKind.GROUP_CREATE, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());
        assertContains(mes, mGroup.getName());
        assertContains(mes, mGroup.getUsers().get(0).getId().getId());
        assertContains(mes, mGroup.getNodes().get(0).getId().getId());

    }

    @Test
    public void groupInfoRequestTest() {

        GroupInfoRequest req = new GroupInfoRequest(mUser.getId(), mGroup.getId());
        assertEquals(ServiceRequest.RequestKind.GROUP_INFO, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());
        assertContains(mes, mGroup.getId());

    }

    @Test
    public void groupMessageRequestTest() {

        GroupMessageRequest req =
                new GroupMessageRequest(mMessage, mGroup.getId(), Group.GroupType.PRIVATE);
        assertEquals(mMessage, req.getMessage());
        assertEquals(mGroup.getId(), req.getReceivingNodeId());
        assertEquals(ServiceRequest.RequestKind.GROUP_MESSAGE, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());
        assertContains(mes, mGroup.getId());
        assertContains(mes, mMessage.getContent().getTextForRequest());

    }

    @Test
    public void groupUpdateImageRequestTest() {

        GroupUpdateImageRequest req = new GroupUpdateImageRequest(mUser, mGroup.getId(), mImage,
                Group.GroupType.PRIVATE);
        assertEquals(mGroup.getId(), req.getGroupId());
        assertEquals(mImage.getByteCount(), req.getNewGroupImage().getByteCount());
        assertEquals(ServiceRequest.RequestKind.GROUP_UPDATE_IMAGE, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());
        assertContains(mes, mGroup.getId());

    }

    @Test
    public void groupUpdateNameRequestTest() {

        String newName = "new name";
        GroupUpdateNameRequest req = new GroupUpdateNameRequest(mUser, mGroup.getId(), newName,
                Group.GroupType.PRIVATE);;
        assertEquals(mGroup.getId(), req.getGroupId());
        assertEquals(newName, req.getNewGroupName());
        assertEquals(ServiceRequest.RequestKind.GROUP_UPDATE_NAME, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());
        assertContains(mes, mGroup.getId());
        assertContains(mes, newName);

    }

    @Test
    public void groupUpdateUsersRequestTest() {

        List<User> newUsers = new ArrayList<>();
        newUsers.add(mUser);
        GroupUpdateUsersRequest req = new GroupUpdateUsersRequest(mUser.getId(), mGroup.getId(),
                newUsers, GroupUpdateUsersRequest.UpdateType.ADD,
                Group.GroupType.PRIVATE);;
        assertEquals(mGroup.getId(), req.getGroupId());
        assertEquals(GroupUpdateUsersRequest.UpdateType.ADD, req.getUpdateType());
        assertEquals(ServiceRequest.RequestKind.GROUP_UPDATE_USERS, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());
        assertContains(mes, mGroup.getId());

    }

    @Test
    public void mediaMessageRequestTest() {

        MediaMessageRequest req = new MediaMessageRequest(mMessage, mGroup.getId());
        assertEquals(mGroup.getId(), req.getReceivingNodeId());
        assertEquals(mMessage.getContent().getTextForRequest(),
                req.getMessage().getContent().getTextForRequest());
        assertEquals(ServiceRequest.RequestKind.MEDIA_MESSAGE, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());
        assertContains(mes, mGroup.getId());
        assertContains(mes, mMessage.getContent().getTextForRequest());

    }

    @Test
    public void nodeHistoryRequestTest() {

        Date now = new Date();
        NodeHistoryRequest req = new NodeHistoryRequest(mGroup.getId(), now);
        assertEquals(mGroup.getId(), req.getGroup());
        assertEquals(now, req.getDate());
        assertEquals(ServiceRequest.RequestKind.NODE_HISTORY, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());
        assertContains(mes, mGroup.getId());

    }

    @Test
    public void nodeSearchRequestTest() {

        String pattern = "search pattern";
        NodeSearchRequest req = new NodeSearchRequest(mUser.getId(), pattern);
        assertEquals(ServiceRequest.RequestKind.NODE_SEARCH, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());
        assertContains(mes, pattern);

    }

    @Test
    public void UserConnectRequestTest() {

        UserConnectRequest req = new UserConnectRequest(mClientUser);
        assertEquals(ServiceRequest.RequestKind.USER_CONNECT, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mClientUser.getId());

    }

    @Test
    public void userEntourageAddRequestTest() {

        UserEntourageAddRequest req = new UserEntourageAddRequest(mUser.getId(), mUser.getId());
        assertEquals(mUser.getId(), req.getUserToAdd());
        assertEquals(ServiceRequest.RequestKind.USER_ENTOURAGE_ADD, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());

    }

    @Test
    public void userEntourageRemoveRequestTest() {

        UserEntourageRemoveRequest req =
                new UserEntourageRemoveRequest(mUser.getId(), mUser.getId());
        assertEquals(mUser.getId(), req.getUserToRemove());
        assertEquals(ServiceRequest.RequestKind.USER_ENTOURAGE_REMOVE, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());

    }

    @Test
    public void userGroupListRequestRequestTest() {

        UserGroupListRequest req = new UserGroupListRequest(mUser);
        assertEquals(ServiceRequest.RequestKind.USER_NODE_LIST, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());

    }

    @Test
    public void userInfoRequestRequestTest() {

        UserInfoRequest req = new UserInfoRequest(mUser, mUser.getId());
        assertEquals(mUser.getId(), req.getUserInfoId());
        assertEquals(ServiceRequest.RequestKind.USER_INFO, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());

    }

    @Test
    public void userSearchRequestTest() {

        String newEmail = "yes@yields.im";
        UserSearchRequest req = new UserSearchRequest(mUser.getId(), newEmail);
        assertEquals(ServiceRequest.RequestKind.USER_SEARCH, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());
        assertContains(mes, newEmail);

    }

    @Test
    public void userUpdateNameRequestTest() {

        UserUpdateNameRequest req = new UserUpdateNameRequest(mUser);
        assertEquals(mUser.getId(), req.getUser().getId());
        assertEquals(ServiceRequest.RequestKind.USER_UPDATE_NAME, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());

    }

    @Test
    public void userUpdateRequestTest() {

        UserUpdateRequest req = new UserUpdateRequest(mUser);
        assertEquals(mUser.getId(), req.getUser().getId());
        assertEquals(ServiceRequest.RequestKind.USER_UPDATE, req.getType());
        String mes = req.parseRequestForServer().message();
        assertContains(mes, mUser.getId());

    }

    private void assertContains(Object container, Object given) {
        assertTrue(container.toString() + " did not contain " + given.toString(),
                container.toString().contains(given.toString()));
    }

}
