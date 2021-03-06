package yields.client.service;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import yields.client.activities.NotifiableActivity;
import yields.client.cache.CacheDatabaseHelper;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.ImageSerialization;
import yields.client.serverconnection.Response;
import yields.client.servicerequest.GroupCreateRequest;
import yields.client.servicerequest.NodeHistoryRequest;
import yields.client.servicerequest.NodeInfoRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserEntourageAddRequest;
import yields.client.servicerequest.UserGroupListRequest;
import yields.client.servicerequest.UserInfoRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Handles Responses from the server.
 * It notifies the app and updates the cache according to the Response from the server.
 */
public class ResponseHandler {

    private final YieldService mService;
    private final CacheDatabaseHelper mCacheHelper;

    /**
     * Main constructor for a ResponseHandler.
     *
     * @param cacheDatabaseHelper The helper for the associated cache.
     * @param yieldService        The service which uses this handler.
     */
    public ResponseHandler(CacheDatabaseHelper cacheDatabaseHelper, YieldService yieldService) {
        mCacheHelper = cacheDatabaseHelper;
        mService = yieldService;
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleUserSearchResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long uid = response.getLong("uid");
            if (uid != 0) {
                Id id = new Id(uid);
                ServiceRequest addToEntourageRequest =
                        new UserEntourageAddRequest(YieldsApplication.getUser().getId(), id);
                mService.sendRequest(addToEntourageRequest);
                mService.notifyChange(NotifiableActivity.Change.ADD_ENTOURAGE);
            } else {
                mService.notifyChange(NotifiableActivity.Change.NOT_EXIST);
            }

        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleNodeSearchResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray nodes = response.getJSONArray("nodes");
            JSONArray names = response.getJSONArray("names");
            JSONArray pics = response.getJSONArray("pics");

            assert (nodes.length() == names.length() && nodes.length() == pics.length());

            int nodeCount = nodes.length();
            ArrayList<Group> groupList = new ArrayList<>();

            for (int i = 0; i < nodeCount; i++) {
                Id id = new Id(nodes.getLong(i));
                String nodeName = names.getString(i);
                Bitmap image = ImageSerialization.unSerializeImage(pics.getString(i));

                if (image == null) {
                    image = YieldsApplication.getDefaultGroupImage();
                }

                groupList.add(new Group(nodeName, id, new ArrayList<Id>(), image));
            }

            YieldsApplication.getGroupsSearched().clear();
            YieldsApplication.getGroupsSearched().addAll(groupList);

            mService.notifyChange(NotifiableActivity.Change.GROUP_SEARCH);

        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleRSSInfoResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            Id nid = new Id(response.getLong("nid"));
            String name = response.getString("name");

            Group rss = YieldsApplication.getUser().getNodeFromId(nid);
            rss.setName(name);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleGroupInfoResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            long nid = response.getLong("nid");
            Id gId = new Id(nid);
            Group group = YieldsApplication.getUser().getGroup(gId);


            ArrayList<Id> userList = new ArrayList<>();
            for (int i = 0; i < users.length(); i++) {
                Id userId = new Id(users.getLong(i));
                userList.add(userId);
                if (YieldsApplication.getUserFromId(userId) == null) {
                    User newUser = new User(userId);
                    YieldsApplication.addNotKnown(newUser);
                    ServiceRequest userInfo =
                            new UserInfoRequest(YieldsApplication.getUser(), userId);
                    mService.sendRequest(userInfo);
                }
            }

            ArrayList<Id> nodeList = new ArrayList<>();
            for (int i = 0; i < nodes.length(); i++) {
                Id nodeId = new Id(nodes.getLong(i));
                nodeList.add(nodeId);
                if (YieldsApplication.getNodeFromId(nodeId) == null) {
                    Group newNode = new Group("", nodeId, new ArrayList<Id>());
                    YieldsApplication.getUser().addNode(newNode);
                    ServiceRequest nodeInfo =
                            new NodeInfoRequest(YieldsApplication.getUser().getId(), nodeId);
                    mService.sendRequest(nodeInfo);
                }
            }

            String name = response.getString("name");
            String image = response.getString("pic");
            group.setName(name);
            if (!image.equals("")) {
                group.setImage(ImageSerialization.unSerializeImage(image));
            }
            group.updateUsers(userList);
            group.updateNodes(nodeList);

            ServiceRequest historyRequest = new NodeHistoryRequest(group.getId(), new Date());
            mService.sendRequest(historyRequest);

            mCacheHelper.addGroup(group);
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleGroupMessageResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            Date prevDatetime = DateSerialization.dateSerializer
                    .toDate(serverResponse.getMetadata().getString("ref"));
            Date serverDatetime = DateSerialization.dateSerializer
                    .toDate(response.getString("datetime"));

            long nid = response.getLong("nid");
            Id id = new Id(nid);

            long contentNid = response.optLong("contentNid", -1);

            if (YieldsApplication.getUser().getGroup(id).getLastUpdate().before(serverDatetime)) {
                YieldsApplication.getUser().getGroup(id)
                        .setLastUpdate(serverDatetime);
            }

            Message message = YieldsApplication.getUser().getGroup(id).updateMessageIdDateAndStatus(new Id(contentNid),
                    prevDatetime, serverDatetime);
            Message copyMessage = new Message(message.getCommentGroupId(), message.getSender(),
                    message.getContent(), prevDatetime, message.getStatus());

            mCacheHelper.deleteMessage(copyMessage, id);
            mCacheHelper.addMessage(message, id);

            mService.notifyChange(NotifiableActivity.Change.MESSAGES_RECEIVE);
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handlePublisherCreateResponse(Response serverResponse) {
        try {
            Id nid = new Id(serverResponse.getMessage().getLong("nid"));
            YieldsApplication.getUser().activateGroup(DateSerialization.dateSerializer
                    .toDate(serverResponse.getMetadata().getString("ref")), nid);
            Group group = YieldsApplication.getUser().getGroup(nid);
            mCacheHelper.addGroup(group);
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handlePublisherInfoResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");
            JSONArray tags = response.getJSONArray("tags");
            String name = response.getString("name");
            long nid = response.getLong("nid");
            String image = response.getString("pic");

            Bitmap pic = image.equals("") ? YieldsApplication.getDefaultGroupImage() : ImageSerialization
                    .unSerializeImage(image);

            ArrayList<Id> userList = new ArrayList<>();
            for (int i = 0; i < users.length(); i++) {
                Id userId = new Id(users.getLong(i));
                userList.add(userId);
                if (YieldsApplication.getUserFromId(userId) == null) {
                    User newUser = new User(userId);
                    YieldsApplication.addNotKnown(newUser);
                    ServiceRequest userInfo =
                            new UserInfoRequest(YieldsApplication.getUser(), userId);
                    mService.sendRequest(userInfo);
                }
            }

            ArrayList<Id> nodeList = new ArrayList<>();
            for (int i = 0; i < nodes.length(); i++) {
                Id nodeId = new Id(nodes.getLong(i));
                nodeList.add(nodeId);
                if (YieldsApplication.getNodeFromId(nodeId) == null) {
                    Group newNode = new Group("", nodeId, new ArrayList<Id>());
                    YieldsApplication.getUser().addNode(newNode);
                    ServiceRequest nodeInfo =
                            new NodeInfoRequest(YieldsApplication.getUser().getId(), nodeId);
                    mService.sendRequest(nodeInfo);
                }
            }

            Group group = YieldsApplication.getUser().getGroup(new Id(nid));

            if (group == null) {
                group = YieldsApplication.getUser().getNodeFromId(new Id(nid));
            } else {
                ServiceRequest historyRequest = new NodeHistoryRequest(group.getId(), new Date());
                mService.sendRequest(historyRequest);
            }

            if (group == null) {
                group = new Group(name, new Id(nid), userList, pic, Group.GroupType.PUBLISHER, true, new Date());
                group.updateNodes(nodeList);
                for (int i = 0; i < tags.length(); i++) {
                    group.addTag(new Group.Tag(tags.getString(i)));
                }
                YieldsApplication.getUser().addGroup(group);
                YieldsApplication.getUser().addNode(group);

            }

            group.setName(name);
            group.setImage(pic);
            group.updateNodes(nodeList);
            group.updateUsers(userList);
            group.setType(Group.GroupType.PUBLISHER);

            mCacheHelper.addGroup(group);
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handlePublisherMessageResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Date prevDatetime = DateSerialization.dateSerializer
                    .toDate(serverResponse.getMetadata().getString("ref"));
            Date serverDatetime = DateSerialization.dateSerializer
                    .toDate(response.getString("datetime"));
            Id id = new Id(nid);

            long contentNid = response.optLong("contentNid", -1);

            if (YieldsApplication.getUser().getGroup(id).getLastUpdate().before(serverDatetime)) {
                YieldsApplication.getUser().getGroup(id)
                        .setLastUpdate(serverDatetime);
            }

            Message message = YieldsApplication.getUser().getGroup(id).updateMessageIdDateAndStatus(new Id(contentNid),
                    prevDatetime, serverDatetime);
            Message copyMessage = new Message(message.getCommentGroupId(), message.getSender(),
                    message.getContent(), prevDatetime, message.getStatus());

            mCacheHelper.deleteMessage(copyMessage, id);
            mCacheHelper.addMessage(message, id);

            mService.notifyChange(NotifiableActivity.Change.MESSAGES_RECEIVE);
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleRSSCreateResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Id id = new Id(nid);

            if (id.getId() != 0) {
                Group rss = YieldsApplication.getUser().getGroupFromRef(DateSerialization.dateSerializer
                        .toDate(serverResponse.getMetadata().getString("ref")));

                rss.setId(id);

                YieldsApplication.getUser().addNode(rss);
                Group group = new Group(rss.getName(), new Id(-1), new ArrayList<Id>(), id);
                YieldsApplication.getUser().addGroup(group);

                ServiceRequest groupCreate = new GroupCreateRequest(YieldsApplication.getUser(), group);
                mService.sendRequest(groupCreate);
                mService.notifyChange(NotifiableActivity.Change.RSS_CREATE);
            } else {
                mService.notifyChange(NotifiableActivity.Change.RSS_FAIL);
            }
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }


    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleNodeMessageBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();

            long contentNid = response.optLong("contentNid", -1);
            Message message = new Message(response.getString("datetime"), contentNid,
                    response.getLong("sender"), response.getString("text"),
                    response.optString("contentType"), response.optString("content"));

            Id groupId = new Id(response.getLong("nid"));

            if (contentNid != -1) {
                YieldsApplication.getUser().addCommentGroup(
                        Group.createGroupForMessageComment(message,
                                YieldsApplication.getUser().getGroup(groupId)));
            }

            mCacheHelper.addMessage(message, groupId);
            mService.receiveMessage(groupId, message);
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString() + " because of : " + e.getMessage());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleUserUpdateBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            User user;
            Id newId = new Id(response.getLong("uid"));
            Boolean newUser = false;

            if (YieldsApplication.getUserFromId(newId) == null) {
                user = new User(newId);
                newUser = true;
            } else {
                user = YieldsApplication.getUserFromId(newId);
            }

            user.setName(response.getString("name"));
            user.setEmail(response.getString("email"));
            String optString = response.optString("pic");

            if (!optString.equals("")) {
                user.setImage(ImageSerialization.unSerializeImage(optString));
            } else {
                user.setImage(YieldsApplication.getDefaultUserImage());
            }

            mCacheHelper.addUser(user);

            for (Group g : YieldsApplication.getUser().getUserGroups()) {
                for (Message m : g.getLastMessages().values()) {
                    if (m.getSender().equals(user.getId())) {
                        m.recomputeView();
                    }
                }
            }

            mService.notifyChange(NotifiableActivity.Change.MESSAGES_RECEIVE);

            if (newUser) {
                YieldsApplication.getUser().addUserToEntourage(user);
            }

            mService.notifyChange(NotifiableActivity.Change.ENTOURAGE_UPDATE);

        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleGroupCreateBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<Id> userList = new ArrayList<>();
            for (int i = 0; i < users.length(); i++) {
                userList.add(new Id(users.getLong(i)));
            }

            ArrayList<Id> nodeList = new ArrayList<>();
            for (int i = 0; i < nodes.length(); i++) {
                nodeList.add(new Id(nodes.getLong(i)));
            }

            Bitmap image = YieldsApplication.getDefaultGroupImage();

            Group newGroup = new Group(name, new Id(nid), userList, image, Group.GroupType.PRIVATE, true, new Date());
            newGroup.updateNodes(nodeList);
            YieldsApplication.getUser().addGroup(newGroup);
            mCacheHelper.addGroup(newGroup);

            for (Id userId : userList) {
                UserInfoRequest userInfoRequest = new UserInfoRequest(YieldsApplication.getUser(), userId);
                mService.sendRequest(userInfoRequest);
            }

            for (Id nodeId : nodeList) {
                NodeInfoRequest nodeInfoRequest = new NodeInfoRequest(YieldsApplication.getUser().getId(), nodeId);
                mService.sendRequest(nodeInfoRequest);
            }
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleGroupUpdateBroadcast(Response serverResponse) {
        UserGroupListRequest request = new UserGroupListRequest(YieldsApplication.getUser());
        mService.sendRequest(request);
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleGroupMessageBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();

            long contentNid = response.optLong("contentNid", -1);


            Message message = new Message(response.getString("datetime"), contentNid,
                    response.getLong("sender"), response.getString("text"),
                    response.optString("contentType"), response.optString("content"));

            Id groupId = new Id(response.getLong("nid"));

            mCacheHelper.addMessage(message, groupId);
            mService.receiveMessage(groupId, message);
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString() + " because of : " + e.getMessage());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handlePublisherCreateBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<Id> userList = new ArrayList<>();
            for (int i = 0; i < users.length(); i++) {
                userList.add(new Id(users.getLong(i)));
            }

            ArrayList<Id> nodeList = new ArrayList<>();
            for (int i = 0; i < nodes.length(); i++) {
                nodeList.add(new Id(nodes.getLong(i)));
            }

            Bitmap image = YieldsApplication.getDefaultGroupImage();

            Group newGroup = new Group(name, new Id(nid), userList, image, Group.GroupType.PUBLISHER, true, new Date());
            newGroup.updateNodes(nodeList);
            YieldsApplication.getUser().addGroup(newGroup);
            mCacheHelper.addGroup(newGroup);

            for (Id userId : userList) {
                UserInfoRequest userInfoRequest = new UserInfoRequest(YieldsApplication.getUser(), userId);
                mService.sendRequest(userInfoRequest);
            }

            for (Id nodeId : nodeList) {
                NodeInfoRequest nodeInfoRequest = new NodeInfoRequest(YieldsApplication.getUser().getId(), nodeId);
                mService.sendRequest(nodeInfoRequest);
            }
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handlePublisherUpdateBroadcast(Response serverResponse) {
        UserGroupListRequest request = new UserGroupListRequest(YieldsApplication.getUser());
        mService.sendRequest(request);
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleRSSCreateBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            String url = response.getString("url");
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleGroupCreateResponse(Response serverResponse) {
        try {
            Id groupId = new Id(serverResponse.getMessage().getLong("nid"));
            YieldsApplication.getUser().activateGroup(DateSerialization.dateSerializer
                    .toDate(serverResponse.getMetadata().getString("ref")), groupId);
            Group group = YieldsApplication.getUser().getGroup(groupId);
            mCacheHelper.addGroup(group);
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleUserInfoResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            Id infoId = new Id(response.getLong("uid"));
            User user = YieldsApplication.getUserFromId(infoId);
            if (response.getString("email").equals("")) {
                if (user != null) {
                    user.update(response);

                    Bitmap image;
                    if (!response.getString("pic").equals("")) {
                        image = ImageSerialization.unSerializeImage(response.getString("pic"));
                    } else {
                        image = YieldsApplication.getDefaultUserImage();
                    }
                    String name = response.getString("name");
                    String email = response.getString("email");
                    User receivedUser = new User(name, infoId, email, image);
                    mCacheHelper.addUser(receivedUser);
                } else {
                    user = new User(response);
                    mCacheHelper.addUser(user);
                    YieldsApplication.addNotKnown(user);
                }
            } else {
                if (YieldsApplication.getUser().getId().equals(infoId)) {

                    ClientUser clientUser = YieldsApplication.getUser();

                    clientUser.update(response);

                    Bitmap image;
                    if (!response.getString("pic").equals("")) {
                        image = ImageSerialization.unSerializeImage(response.getString("pic"));
                    } else {
                        image = YieldsApplication.getDefaultUserImage();
                    }
                    String name = response.getString("name");
                    String email = response.getString("email");
                    User receivedUser = new User(name, infoId, email, image);
                    mCacheHelper.addUser(receivedUser);

                    JSONArray entourage = response.getJSONArray("entourage");
                    JSONArray entourageRefreshedAt = response.getJSONArray("entourageUpdatedAt");

                    if (entourage != null && entourageRefreshedAt != null) {
                        for (int i = 0; i < entourage.length(); i++) {
                            User newUser = new User(new Id(entourage.getLong(i)));
                            clientUser.addUserToEntourage(newUser);
                            ServiceRequest userInfoRequest = new UserInfoRequest(YieldsApplication.getUser(),
                                    new Id(entourage.getLong(i)));

                            mService.sendRequest(userInfoRequest);
                        }
                    }

                    ServiceRequest groupList = new UserGroupListRequest(clientUser);
                    mService.sendRequest(groupList);

                } else {
                    if (user == null) {
                        user = new User(response);
                        YieldsApplication.getUser().addUserToEntourage(user);
                        mCacheHelper.addUser(user);
                    } else {
                        Bitmap image;
                        if (!response.getString("pic").equals("")) {
                            image = ImageSerialization.unSerializeImage(response.getString("pic"));
                        } else {
                            image = YieldsApplication.getDefaultUserImage();
                        }
                        String name = response.getString("name");
                        String email = response.getString("email");
                        User receivedUser = new User(name, infoId, email, image);
                        mCacheHelper.addUser(receivedUser);
                        user.update(response);
                    }

                }
            }
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleUserNodeListResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray groupsId = response.getJSONArray("nodes");
            JSONArray updatedAt = response.getJSONArray("updatedAt");
            JSONArray refreshedAt = response.getJSONArray("refreshedAt");

            int groupCount = groupsId.length();
            assert (groupCount == updatedAt.length() &&
                    groupCount == refreshedAt.length());

            for (int i = 0; i < groupCount; i++) {

                Group group = mCacheHelper.getGroup(new Id(Long.valueOf(groupsId.getString(i))));

                if (group == null) {
                    group = new Group(groupsId.getString(i), "placeholder", refreshedAt.getString(i));
                }

                YieldsApplication.getUser().addGroup(group);
                ServiceRequest groupInfo =
                        new NodeInfoRequest(YieldsApplication.getUser().getId(), group.getId());
                mService.sendRequest(groupInfo);
            }

            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleUserConnectResponse(Response serverResponse) {
        ClientUser user = YieldsApplication.getUser();
        try {
            user.setId(new Id(serverResponse.getMessage().getLong("uid")));
            if (serverResponse.getMessage().getBoolean("returning")) {
                ServiceRequest userInfoRequest = new UserInfoRequest(user, user.getId());
                mService.sendRequest(userInfoRequest);
                mService.notifyChange(NotifiableActivity.Change.CONNECTED);
            } else {
                mService.notifyChange(NotifiableActivity.Change.NEW_USER);
            }
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleNodeHistoryResponse(Response serverResponse) {
        try {
            long nid = serverResponse.getMessage().getLong("nid");
            JSONArray datetimes = serverResponse.getMessage().getJSONArray("datetimes");
            JSONArray senders = serverResponse.getMessage().getJSONArray("senders");
            JSONArray texts = serverResponse.getMessage().getJSONArray("texts");
            JSONArray contentTypes = serverResponse.getMessage().getJSONArray("contentTypes");
            JSONArray contents = serverResponse.getMessage().getJSONArray("contents");
            JSONArray contentNids = serverResponse.getMessage().getJSONArray("contentNids");

            int count = datetimes.length();
            assert (count == senders.length() && count == texts.length() && count == contentTypes
                    .length() && count == contents.length());

            Id groupId = new Id(nid);
            ArrayList<Message> messageList = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Long contentNid = contentNids.optLong(i, -1);

                Message message = new Message(datetimes.getString(i), contentNid, senders.getLong(i), texts
                        .getString(i), contentTypes.getString(i), contents.getString(i));

                if (contentNid != -1) {
                    YieldsApplication.getUser().addCommentGroup(
                            Group.createGroupForMessageComment(message,
                                    YieldsApplication.getUser().getGroup(groupId)));
                }

                messageList.add(message);
                mCacheHelper.addMessage(message, groupId);
            }
            mService.receiveMessages(groupId, messageList);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the appropriate Response which is given to it by argument.
     */
    protected void handleMediaMessageResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            Date prevDatetime = DateSerialization.dateSerializer
                    .toDate(serverResponse.getMetadata().getString("ref"));
            Date serverDatetime = DateSerialization.dateSerializer
                    .toDate(response.getString("datetime"));

            long nid = response.getLong("nid");
            Id id = new Id(nid);

            Message message = YieldsApplication.getUser().getCommentGroup(id).updateMessageIdDateAndStatus(new Id(-1),
                    prevDatetime, serverDatetime);
            Message copyMessage = new Message(message.getCommentGroupId(), message.getSender(),
                    message.getContent(), prevDatetime, message.getStatus());

            mCacheHelper.deleteMessage(copyMessage, id);
            mCacheHelper.addMessage(message, id);
            mService.notifyChange(NotifiableActivity.Change.MESSAGES_RECEIVE);

        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }
}
