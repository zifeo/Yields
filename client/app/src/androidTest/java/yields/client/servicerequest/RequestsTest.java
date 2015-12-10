package yields.client.servicerequest;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.test.mock.MockContext;
import android.test.mock.MockResources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import org.junit.Test;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yields.client.generalhelpers.MockModel;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.*;
import static yields.client.activities.MockFactory.*;

public class RequestsTest {

    private final User mUser;
    private final Group mGroup;
    private final Message mMessage;
    private final Bitmap mImage;

    public RequestsTest() {
        new MockModel();
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getTargetContext());
        mUser = generateFakeUser("name", new Id(-1), "test@yields.im");
        mGroup = generateMockGroups(1).get(0);
        mGroup.addUser(mUser.getId());
        mGroup.addNode(mGroup);
        mMessage = generateMockMessage("group", mGroup.getId(), mUser, generateFakeTextContent(0));
        mImage = generateMockImage();
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

        GroupUpdateImageRequest req = new GroupUpdateImageRequest(mUser, mGroup.getId(), mImage);
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
        GroupUpdateNameRequest req = new GroupUpdateNameRequest(mUser, mGroup.getId(), newName);
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
                newUsers, GroupUpdateUsersRequest.UpdateType.ADD);
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

    private void assertContains(Object container, Object given) {
        assertTrue(container.toString() + " did not contain " + given.toString(),
                container.toString().contains(given.toString()));
    }

}
