package yields.client.Requests;

import android.graphics.Bitmap;
<<<<<<< HEAD
import android.util.Base64;

=======

import org.json.JSONArray;
>>>>>>> master
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

<<<<<<< HEAD
import java.nio.ByteBuffer;
import java.util.Date;

=======
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yields.client.activities.MockFactory;
>>>>>>> master
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.TextContent;
import yields.client.node.Group;
<<<<<<< HEAD
=======
import yields.client.node.User;
>>>>>>> master
import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Tests for the RequestBuilder class
 */
public class ServerRequestsTests {

    //TODO: Find a way to approximate Date testing.

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
<<<<<<< HEAD
                    ServiceRequest.RequestKind.USERGROUPLIST.getValue());
=======
                    ServiceRequest.RequestKind.USER_GROUP_LIST.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
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
            String email = "dank@pepe.jpg";
            ServerRequest serverRequest = RequestBuilder.userEntourageAddRequest(senderId, email);

            JSONObject json = new JSONObject(serverRequest.message());
            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.USERENTOURAGEADD.getValue());
=======
                    ServiceRequest.RequestKind.USER_ENTOURAGE_ADD.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.EMAIL.getValue()),
                    email);
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
            String email = "dank@pepe.jpg";
            ServerRequest serverRequest = RequestBuilder.userEntourageRemoveRequest(senderId, email);

            JSONObject json = new JSONObject(serverRequest.message());
            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.USERENTOURAGEREMOVE.getValue());
=======
                    ServiceRequest.RequestKind.USER_ENTOURAGE_REMOVE.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.EMAIL.getValue()),
                    email);
        } catch (JSONException e) {
            fail("Request was not built correctly !");
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
<<<<<<< HEAD
                    ServiceRequest.RequestKind.USERCONNECT.getValue());
=======
                    ServiceRequest.RequestKind.USER_CONNECT.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
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
<<<<<<< HEAD
    /*
=======
>>>>>>> master
    @Test
    public void testGroupCreateRequest(){
        try {
            Id senderId = new Id(11);
            String groupName = "Dank AF";
            List<User> users = MockFactory.generateMockUsers(3);
<<<<<<< HEAD
            List<Id> nodeIds = new ArrayList<>();
            for(User user : users){
=======
            List<String> nodeIdsAsString = new ArrayList<>();
            List<Id> nodeIds = new ArrayList<>();
            for(User user : users){
                nodeIdsAsString.add(user.getId().getId());
>>>>>>> master
                nodeIds.add(user.getId());
            }

            ServerRequest serverRequest = RequestBuilder.groupCreateRequest(senderId, groupName,
<<<<<<< HEAD
                    nodeIds);

            JSONObject json = new JSONObject(serverRequest.message());
            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUPCREATE.getValue());
=======
                    Group.GroupVisibility.PRIVATE, nodeIds);

            JSONObject json = new JSONObject(serverRequest.message());
            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.GROUP_CREATE.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NAME.getValue()),
                    groupName);
            assertEquals(json.getJSONObject("message").getJSONArray(RequestBuilder.Fields.NODES.getValue()),
<<<<<<< HEAD
                    new JSONArray(nodeIds));
=======
                    new JSONArray(nodeIdsAsString));
>>>>>>> master
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }
<<<<<<< HEAD
    */
=======

>>>>>>> master

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
                    newGroupName);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPUPDATENAME.getValue());
=======
                    ServiceRequest.RequestKind.GROUP_UPDATE_NAME.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NAME.getValue()),
                    newGroupName);
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a GroupUpdateVisibilityRequest is correctly built.
     * (Test for groupUpdateVisibilityRequest(Id sender, Id groupId, Group.GroupVisibility newVisibility))
     */
    @Test
    public void testGroupUpdateVisibilityRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);

            ServerRequest serverRequest = RequestBuilder.groupUpdateVisibilityRequest(senderId, groupId,
                    Group.GroupVisibility.PRIVATE);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPUPDATEVISIBILITY.getValue());
=======
                    ServiceRequest.RequestKind.GROUP_UPDATE_VISIBILITY.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.VISIBILITY.getValue()),
                    Group.GroupVisibility.PRIVATE.getValue());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());

            serverRequest = RequestBuilder.groupUpdateVisibilityRequest(senderId, groupId,
                    Group.GroupVisibility.PUBLIC);
            json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPUPDATEVISIBILITY.getValue());
=======
                    ServiceRequest.RequestKind.GROUP_UPDATE_VISIBILITY.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.VISIBILITY.getValue()),
                    Group.GroupVisibility.PUBLIC.getValue());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
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
<<<<<<< HEAD
            //TODO : IMAGE SERIALIZATION EXPLANATION @Trofleb
=======
>>>>>>> master
            Id groupId = new Id(12);
            Id senderId = new Id(11);

            Bitmap newImage = YieldsApplication.getDefaultGroupImage();
<<<<<<< HEAD
            int size = newImage.getRowBytes() * newImage.getHeight();
            ByteBuffer b = ByteBuffer.allocate(size);
            newImage.copyPixelsToBuffer(b);
            byte[] byteImage = b.array();
=======
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            newImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
>>>>>>> master

            ServerRequest serverRequest = RequestBuilder.groupUpdateImageRequest(senderId, groupId,
                    newImage);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPUPDATEIMAGE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.IMAGE.getValue()),
                    Base64.encodeToString(byteImage, Base64.DEFAULT));
=======
                    ServiceRequest.RequestKind.GROUP_UPDATE_IMAGE.getValue());
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.IMAGE.getValue()),
                    stream.toString());
>>>>>>> master
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
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
                    newUserId);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPADD.getValue());
=======
                    ServiceRequest.RequestKind.GROUP_ADD.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NID.getValue()),
                    newUserId.getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
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
                    userToRemoveId);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPREMOVE.getValue());
=======
                    ServiceRequest.RequestKind.GROUP_REMOVE.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.NID.getValue()),
                    userToRemoveId.getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
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
<<<<<<< HEAD
=======
            Bitmap newImage = YieldsApplication.getDefaultGroupImage();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            newImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
>>>>>>> master

            ServerRequest serverRequest = RequestBuilder.groupMessageRequest(senderId, groupId,
                    textContent);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPMESSAGE.getValue());
=======
                    ServiceRequest.RequestKind.GROUP_MESSAGE.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.CONTENT_TYPE.getValue()),
                    textContent.getType().getType());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.TEXT.getValue()),
                    text);

            serverRequest = RequestBuilder.groupMessageRequest(senderId, groupId,
                    imageContent);
            json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPMESSAGE.getValue());
=======
                    ServiceRequest.RequestKind.GROUP_MESSAGE.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.CONTENT_TYPE.getValue()),
                    imageContent.getType().getType());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.TEXT.getValue()),
                    text);
<<<<<<< HEAD
            //TODO : CHECK IMAGE
=======
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.IMAGE.getValue()),
                    stream.toString());
>>>>>>> master
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a GroupTextMessageRequest is correctly built.
     * (Test for groupTextMessageRequest(Id sender, Id groupId, TextContent content))
     */
    @Test
    public void testGroupTextMessageRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);
            String text = "Apple pie is best pie !";
            TextContent textContent = new TextContent(text);

            ServerRequest serverRequest = RequestBuilder.groupTextMessageRequest(senderId, groupId,
                    textContent);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPMESSAGE.getValue());
=======
                    ServiceRequest.RequestKind.GROUP_MESSAGE.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.CONTENT_TYPE.getValue()),
                    textContent.getType().getType());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.TEXT.getValue()),
                    text);
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a GroupImageMessageRequest is correctly built.
     * (Test for groupImageMessageRequest(Id sender, Id groupId, ImageContent content))
     */
    @Test
    public void testGroupImageMessageRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);
            String text = "Apple pie is best pie !";
<<<<<<< HEAD
            Bitmap image = YieldsApplication.getDefaultGroupImage();
            ImageContent imageContent = new ImageContent(image, text);
=======
            Bitmap newImage = YieldsApplication.getDefaultGroupImage();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            newImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
            ImageContent imageContent = new ImageContent(newImage, text);
>>>>>>> master

            ServerRequest serverRequest = RequestBuilder.groupImageMessageRequest(senderId, groupId,
                    imageContent);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPMESSAGE.getValue());
=======
                    ServiceRequest.RequestKind.GROUP_MESSAGE.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.CONTENT_TYPE.getValue()),
                    imageContent.getType().getType());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.TEXT.getValue()),
                    text);
<<<<<<< HEAD
            //TODO : CHECK IMAGE
=======
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.IMAGE.getValue()),
                    stream.toString());
>>>>>>> master
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a GroupHistoryRequest is correctly built.
     * (Test for groupHistoryRequest(Id senderId, Id groupId, Date last, int messageCount))
     */
    @Test
    public void testGroupHistoryRequest() {
        try {
            Id groupId = new Id(12);
            Id senderId = new Id(11);
            Date date = new Date();
            int messageCount = 10;

            ServerRequest serverRequest = RequestBuilder.groupHistoryRequest(senderId, groupId,
                    date, messageCount);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
<<<<<<< HEAD
                    ServiceRequest.RequestKind.GROUPHISTORY.getValue());
=======
                    ServiceRequest.RequestKind.GROUP_HISTORY.getValue());
>>>>>>> master
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.LAST.getValue()),
                    DateSerialization.toString(date));
            assertEquals(json.getJSONObject("message").getInt(RequestBuilder.Fields.COUNT.getValue
                    ()), messageCount);
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.GID.getValue()),
                    groupId.getId());
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }

    /**
     * Tests if a pingRequest is correctly built.
     * (Test for pingRequest(Id senderId, String content))
     */
    @Test
    public void testPingRequest() {
        try {
            Id senderId = new Id(11);
            String text = "Apple pie is best pie !";

            ServerRequest serverRequest = RequestBuilder.pingRequest(senderId, text);
            JSONObject json = new JSONObject(serverRequest.message());

            assertEquals(json.getString(RequestBuilder.Fields.KIND.getValue()),
                    ServiceRequest.RequestKind.PING.getValue());
            assertEquals(json.getJSONObject("metadata").getString("sender"), senderId
                    .getId());
            assertEquals(json.getJSONObject("message").getString(RequestBuilder.Fields.TEXT.getValue()),
                    text);
        } catch (JSONException e) {
            fail("Request was not built correctly !");
        }
    }
}
