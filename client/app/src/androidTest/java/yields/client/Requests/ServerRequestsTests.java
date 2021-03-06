package yields.client.requests;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yields.client.activities.MockFactory;
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
import yields.client.servicerequest.ServiceRequest;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Tests for the RequestBuilder class
 */
public class ServerRequestsTests {

    /**
     * Tests if a UserGroupListRequest is correctly built.
     * (Test for userGroupListRequest(Id senderId))
     */
    @Test
    public void testUserGroupList() {
        try {
            Id senderId = new Id(11);
            ServerRequest serverRequest = RequestBuilder.userGroupListRequest(senderId);

            JSONObject json = new JSONObject(serverRequest.message());
            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.USER_NODE_LIST.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a UserEntourageAddRequest is correctly built.
     * (Test for userEntourageAddRequest(Id senderId, String email))
     */
    @Test
    public void testUserEntourageAddRequest() {
        try {
            Id senderId = new Id(11);
            Id entourageId = new Id(12);
            ServerRequest serverRequest = RequestBuilder.userEntourageAddRequest(senderId, entourageId);

            JSONObject json = new JSONObject(serverRequest.message());
            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.USER_UPDATE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertTrue(json.getJSONObject("message").getJSONArray(
                    RequestBuilder.Fields.ADD_ENTOURAGE.getValue()).getLong(0) == entourageId.getId());
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a UserEntourageRemoveRequest is correctly built.
     * (Test for userEntourageRemoveRequest(Id senderId, String email))
     */
    @Test
    public void testUserEntourageRemoveRequest() {
        try {
            Id senderId = new Id(11);
            Id addId = new Id(12);
            ServerRequest serverRequest = RequestBuilder.userEntourageRemoveRequest(senderId, addId);

            JSONObject json = new JSONObject(serverRequest.message());
            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.USER_UPDATE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertEquals(json.getJSONObject("message").getJSONArray(
                            RequestBuilder.Fields.REMOVE_ENTOURAGE.getValue()).getLong(0),
                    addId.getId().longValue());
        } catch (JSONException e) {
            fail("Request was not built correctly ! " + e.getMessage());
        }
    }

    /**
     * Tests if a UserConnectRequest is correctly built.
     * (Test for userConnectRequest(Id senderId, String email))
     */
    @Test
    public void testUserConnectRequest() {
        try {
            Id senderId = new Id(11);
            String email = "dank@pepe.jpg";
            ServerRequest serverRequest = RequestBuilder.userConnectRequest(senderId, email);

            JSONObject json = new JSONObject(serverRequest.message());
            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.USER_CONNECT.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.EMAIL.getValue()),
                    email);
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a GroupCreateRequest is correctly built.
     * (Test for groupCreateRequest(Id sender, String name, List<Id> nodes))
     */
    @Test
    public void testGroupCreateRequest(){
        try {
            YieldsApplication.setUser(new ClientUser("test",new Id(-1), "test@epfl.ch",
                    YieldsApplication.getDefaultUserImage()));
            Id senderId = new Id(11);
            String groupName = "Dank AF";
            List<Id> users = MockFactory.generateMockUsers(3);
            List<Long> userIdsAsString = new ArrayList<>();
            List<Id> userIds = new ArrayList<>();
            List<Id> nodeIds = new ArrayList<>();
            for(Id user : users){
                userIdsAsString.add(user.getId());
                userIds.add(user);
            }

            Group group = new Group(groupName, new Id(0), userIds, YieldsApplication.getDefaultGroupImage(),
                    Group.GroupType.PRIVATE, true, new Date());
            ServerRequest serverRequest = RequestBuilder.groupCreateRequest(senderId,
                    group, userIds, nodeIds);

            JSONObject json = new JSONObject(serverRequest.message());
            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUP_CREATE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NAME
                            .getValue()), groupName);
            assertEquals(json.getJSONObject("message").getJSONArray(RequestBuilder.Fields.USERS
                    .getValue()).toString(), new JSONArray(userIdsAsString).toString());
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a GroupUpdateNameRequest is correctly built.
     * (Test for groupUpdateNameRequest(Id sender, Id groupId, String newName))
     */
    @Test
    public void testGroupUpdateNameRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);
            String newGroupName = "What a great group.jpg";

            ServerRequest serverRequest = RequestBuilder.groupUpdateNameRequest(senderId, groupId,
                    newGroupName, Group.GroupType.PUBLISHER);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUP_UPDATE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NAME.getValue()),
                    newGroupName);
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NID.getValue()),
                    groupId.getId().toString());
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a GroupUpdateImageRequest is correctly built.
     * (Test for groupUpdateImageRequest(Id sender, Id groupId, ImageContent newImage))
     */
    @Test
    public void testGroupUpdateImageRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);

            Bitmap newImage = YieldsApplication.getDefaultGroupImage();
            String imageEncoded = ImageSerialization.serializeImage(newImage,
                    ImageSerialization.SIZE_IMAGE_NODE);

            ServerRequest serverRequest = RequestBuilder.groupUpdateImageRequest(senderId, groupId,
                    newImage, Group.GroupType.PRIVATE);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUP_UPDATE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.IMAGE.getValue()),
                    imageEncoded);
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NID.getValue()),
                    groupId.getId().toString());
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }


    /**
     * Tests if a GroupAddRequest is correctly built.
     * (Test for groupAddRequest(Id sender, Id groupId, Id newUser))
     */
    @Test
    public void testGroupAddRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);
            Id newUserId = new Id(14);

            ServerRequest serverRequest = RequestBuilder.groupAddRequest(senderId, groupId,
                    newUserId, Group.GroupType.PRIVATE);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUP_UPDATE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a GroupRemoveRequest is correctly built.
     * (Test for groupRemoveRequest(Id sender, Id groupId, Id userToRemove))
     */
    @Test
    public void testGroupRemoveRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);
            Id userToRemoveId = new Id(14);

            ServerRequest serverRequest = RequestBuilder.groupRemoveRequest(senderId, groupId,
                    userToRemoveId, Group.GroupType.PRIVATE);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUP_UPDATE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a groupMessageRequest is correctly built.
     * (Test for groupMessageRequest(Id sender, Id groupId, Content content))
     */
    @Test
    public void testGroupMessageRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);
            String text = "Apple pie is best pie !";
            Content textContent = new TextContent(text);
            Content imageContent = new ImageContent(YieldsApplication.getDefaultGroupImage(), text);
            Bitmap newImage = YieldsApplication.getDefaultGroupImage();
            String imageEncoded = ImageSerialization.serializeImage(newImage,
                    ImageSerialization.SIZE_IMAGE);

            ServerRequest serverRequest = RequestBuilder.groupMessageRequest(senderId, groupId,
                    Group.GroupType.PRIVATE, textContent, new Date());
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUP_MESSAGE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NID.getValue()),
                    groupId.getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.CONTENT_TYPE.getValue()),
                    "null");
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.TEXT.getValue()),
                    text);

            serverRequest = RequestBuilder.groupMessageRequest(senderId, groupId,
                    Group.GroupType.PRIVATE, imageContent, new Date());
            json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUP_MESSAGE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NID.getValue()),
                    groupId.getId().toString());
            assertEquals(imageContent.getType().toString().toLowerCase(),
                    json.getJSONObject("message").getString(RequestBuilder.Fields.CONTENT_TYPE.getValue()));
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.TEXT.getValue()),
                    text);
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.CONTENT.getValue()),
                    imageEncoded);
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a GroupTextMessageRequest is correctly built.
     * (Test for nodeTextMessageRequest(Id sender, Id groupId, TextContent content))
     */
    @Test
    public void testGroupTextMessageRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);
            String text = "Apple pie is best pie !";
            TextContent textContent = new TextContent(text);

            ServerRequest serverRequest = RequestBuilder.groupMessageRequest(senderId, groupId,
                    Group.GroupType.PRIVATE, textContent, new Date());
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUP_MESSAGE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NID.getValue()),
                    groupId.getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.CONTENT_TYPE.getValue()),
                    "null");
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.TEXT.getValue()),
                    text);
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a GroupImageMessageRequest is correctly built.
     * (Test for nodeImageMessageRequest(Id sender, Id groupId, ImageContent content))
     */
    @Test
    public void testGroupImageMessageRequest() {
        try {
            Id groupId = new Id(12);
            Group g = new Group("", groupId, new ArrayList<Id>(), YieldsApplication.getDefaultGroupImage());
            Id senderId = new Id(11);
            String text = "Apple pie is best pie !";
            Bitmap newImage = YieldsApplication.getDefaultGroupImage();
            ImageContent imageContent = new ImageContent(newImage, text);
            String imageEncoded = ImageSerialization.serializeImage(newImage,
                    ImageSerialization.SIZE_IMAGE);

            ServerRequest serverRequest = RequestBuilder.groupMessageRequest(senderId, groupId,
                    Group.GroupType.PRIVATE, imageContent, new Date());
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUP_MESSAGE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NID.getValue()),
                    groupId.getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.CONTENT_TYPE.getValue()),
                    imageContent.getType().toString().toLowerCase());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.TEXT.getValue()),
                    text);
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.CONTENT.getValue()),
                    imageEncoded);
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a nodeHistoryRequest is correctly built.
     * (Test for nodeHistoryRequest(Id senderId, Id groupId, Date last, int messageCount))
     */
    @Test
    public void testNodeHistoryRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);
            Date date = new Date();
            int messageCount = 10;

            ServerRequest serverRequest = RequestBuilder.nodeHistoryRequest(senderId, groupId,
                    date, messageCount);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.NODE_HISTORY.getValue());
            assertEquals(json.getJSONObject("metadata").getString("client"), senderId
                    .getId().toString());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.LAST.getValue()),
                    DateSerialization.dateSerializer.toString(date));
            assertEquals(json.getJSONObject("message").getInt(RequestBuilder.Fields.COUNT.getValue
                    ()), messageCount);
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NID.getValue()),
                    groupId.getId().toString());
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }
}
