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
import yields.client.exceptions.CacheDatabaseException;
import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.ImageSerialization;
import yields.client.serverconnection.Response;
import yields.client.servicerequest.GroupInfoRequest;
import yields.client.servicerequest.NodeHistoryRequest;
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

    protected void handleUserUpdateResponse(Response serverResponse) {
        Log.d("Y:" + this.getClass().getName(), "Response for UserUpdate");
        ServiceRequest userInfo = new UserInfoRequest(YieldsApplication.getUser(),
                YieldsApplication.getUser().getId());
        mService.sendRequest(userInfo);
    }

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

    protected void handleNodeSearchResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray nodes = response.getJSONArray("nodes");
            JSONArray names = response.getJSONArray("names");
            JSONArray pics = response.getJSONArray("pic");

            assert (nodes.length() == names.length() && nodes.length() == pics.length());

            int nodeCount = nodes.length();
            ArrayList<Group> groupList = new ArrayList<>();

            for (int i = 0; i < nodeCount; i++) {
                Id id = new Id(nodes.getLong(i));
                String nodeName = names.getString(i);
                Bitmap image = ImageSerialization.unSerializeImage(pics.getString(i));

                groupList.add(new Group(nodeName, id, new ArrayList<Id>(), image));
            }

            Date ref = DateSerialization
                    .dateSerializer.toDate(serverResponse.getMetadata().getString("ref"));

            if (ref.getTime() == YieldsApplication.getLastDateSearch().getTime()) {
                YieldsApplication.getGroupsSearched().clear();
                YieldsApplication.getGroupsSearched().addAll(groupList);
            }

            mService.notifyChange(NotifiableActivity.Change.GROUP_SEARCH);

        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupUpdateResponse(Response serverResponse) {
        ServiceRequest groupInfo = new UserGroupListRequest(YieldsApplication.getUser());
        mService.sendRequest(groupInfo);
        // No output.
        // TODO : decide what to do.
    }

    protected void handleGroupInfoResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray users = response.getJSONArray("users");
            // TODO: discuss use of nodes in groups !important
            //JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<Id> userList = new ArrayList<>();
            for (int i = 0; i < users.length(); i++) {
                Id userId = new Id(users.getLong(i));
                userList.add(userId);
                if (YieldsApplication.getUser() == null) {
                    User newUser = new User(userId);
                    YieldsApplication.addNotKnown(newUser);
                    ServiceRequest userInfo =
                            new UserInfoRequest(YieldsApplication.getUser(), userId);
                    mService.sendRequest(userInfo);
                }
            }


            long nid = response.getLong("nid");
            String name = response.getString("name");
            String image = response.getString("pic");
            Group group = YieldsApplication.getUser().modifyGroup(new Id(nid));
            group.setName(name);
            if (!image.equals("")) {
                group.setImage(ImageSerialization.unSerializeImage(image));
            }
            group.updateUsers(userList);

            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupMessageResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Date prevDatetime = DateSerialization.dateSerializer
                    .toDate(serverResponse.getMetadata().getString("ref"));
            Date serverDatetime = DateSerialization.dateSerializer
                    .toDate(response.getString("datetime"));
            Id id = new Id(nid);

            if (YieldsApplication.getUser().modifyGroup(id).getLastUpdate().before(serverDatetime)) {
                YieldsApplication.getUser().modifyGroup(id)
                        .setLastUpdate(serverDatetime);
            }

            YieldsApplication.getUser().modifyGroup(id).validateMessage(prevDatetime, serverDatetime);
            mService.notifyChange(NotifiableActivity.Change.MESSAGES_RECEIVE);
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handlePublisherCreateResponse(Response serverResponse) {
        try {
            // TODO : Change behaviour so that you don't go quit activity as long as group is not accepted.
            YieldsApplication.getUser().activateGroup(
                    new Id(serverResponse.getMessage().getLong("nid")));
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handlePublisherUpdateResponse(Response serverResponse) {
        Log.d("ServiceRequestCtrllr", "Response for Publisher Update.");
        // Nothing to parse.
        // TODO : decide what to do.
    }

    protected void handlePublisherInfoResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray users = response.getJSONArray("users");
            // TODO: discuss use of nodes in groups !important
            //JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<Id> userList = new ArrayList<>();
            for (int i = 0; i < users.length(); i++) {
                Id userId = new Id(users.getLong(i));
                userList.add(userId);
                if (YieldsApplication.getUser() == null) {
                    User newUser = new User(userId);
                    YieldsApplication.addNotKnown(newUser);
                    ServiceRequest userInfo =
                            new UserInfoRequest(YieldsApplication.getUser(), userId);
                    mService.sendRequest(userInfo);
                }
            }


            long nid = response.getLong("nid");
            String name = response.getString("name");
            String image = response.getString("pic");
            Group group = YieldsApplication.getUser().modifyGroup(new Id(nid));
            group.setName(name);
            if (!image.equals("")) {
                group.setImage(ImageSerialization.unSerializeImage(image));
            }
            group.updateUsers(userList);

            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handlePublisherMessageResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Date prevDatetime = DateSerialization.dateSerializer
                    .toDate(serverResponse.getMetadata().getString("ref"));
            Date serverDatetime = DateSerialization.dateSerializer
                    .toDate(response.getString("datetime"));
            Id id = new Id(nid);

            if (YieldsApplication.getUser().modifyGroup(id).getLastUpdate().before(serverDatetime)) {
                YieldsApplication.getUser().modifyGroup(id)
                        .setLastUpdate(serverDatetime);
            }

            YieldsApplication.getUser().modifyGroup(id).validateMessage(prevDatetime, serverDatetime);
            mService.notifyChange(NotifiableActivity.Change.MESSAGES_RECEIVE);
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleRSSCreateResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Id id = new Id(nid);

            // TODO (Nico) : Notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleNodeMessageBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();

            Message message = new Message(response.getString("datetime"),
                    response.getLong("sender"), response.getString("text"),
                    response.getString("contentType"), response.getString("content"));

            Id groupId = new Id(response.getLong("nid"));

            mService.receiveMessage(groupId, message);

        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleUserUpdateBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            User user;
            Boolean newUser = false;

            if (YieldsApplication.getUserFromId(new Id(response.getLong("uid"))) == null) {
                user = new User(new Id(response.getLong("uid")));
                newUser = true;
            } else {
                user = YieldsApplication.getUserFromId(new Id(response.getLong("uid")));
            }

            user.setName(response.getString("name"));
            user.setEmail(response.getString("email"));
            String optString = response.optString("pic");

            if (!optString.equals("")) {
                user.setImg(ImageSerialization.unSerializeImage(optString));
            } else {
                user.setImg(YieldsApplication.getDefaultUserImage());
            }

            if (newUser) {
                //TODO : notify() to add user to entourage -> notification
            }

        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupCreateBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            //TODO: Ask for this !!!!
            //JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<Id> userList = new ArrayList<>();
            for (int i = 0; i < users.length(); i++) {
                userList.add(new Id(users.getLong(i)));
            }

            Group newGroup = new Group(name, new Id(nid), userList);
            newGroup.setValidated();
            YieldsApplication.getUser().addGroup(newGroup);
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupUpdateBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            Id nid = new Id(response.getLong("nid"));
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            // TODO: Ask for this !!!
            //JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<Id> userList = new ArrayList<>();
            for (int i = 0; i < users.length(); i++) {
                userList.add(new Id(users.getLong(i)));
            }

            Group group = YieldsApplication.getUser().modifyGroup(nid);
            group.updateUsers(userList);
            group.setImage(ImageSerialization.unSerializeImage(response.getString("pic")));
            group.setName(name);

            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupMessageBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();

            Message message = new Message(response.getString("datetime"),
                    response.getLong("sender"), response.getString("text"),
                    response.optString("contentType"), response.optString("content"));

            Id groupId = new Id(response.getLong("nid"));

            mService.receiveMessage(groupId, message);
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString() + " because of : " + e.getMessage());
        }
    }

    protected void handlePublisherCreateBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            //TODO: Ask for this !!!!
            //JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<Id> userList = new ArrayList<>();
            for (int i = 0; i < users.length(); i++) {
                userList.add(new Id(users.getLong(i)));
            }

            Group newGroup = new Group(name, new Id(nid), userList);
            YieldsApplication.getUser().addGroup(newGroup);
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handlePublisherUpdateBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            Id nid = new Id(response.getLong("nid"));
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            // TODO: Ask for this !!!
            //JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<Id> userList = new ArrayList<>();
            for (int i = 0; i < users.length(); i++) {
                userList.add(new Id(users.getLong(i)));
            }

            Group group = YieldsApplication.getUser().modifyGroup(nid);
            group.updateUsers(userList);
            group.setImage(ImageSerialization.unSerializeImage(response.getString("pic")));
            group.setName(name);

            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handlePublisherMessageBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();

            Message message = new Message(response.getString("datetime"),
                    response.getLong("sender"), response.getString("text"),
                    response.getString("contentType"), response.getString("content"));

            Id groupId = new Id(response.getLong("nid"));

            mService.receiveMessage(groupId, message);
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    //TODO : Actually don't know what to do yet
    protected void handleRSSCreateBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            String url = response.getString("url");

            // TODO : Create instance of RSS.
            // TODO : (Nico) Notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    //TODO : Actually don't know what to do yet
    protected void handleRSSMessageBroadcast(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();

            long nid = response.getLong("nid");
            Date datetime = DateSerialization.dateSerializer.toDate(response.getString("datetime"));
            long senderId = response.getLong("sender");
            String text = response.getString("text");
            String contentType = response.getString("contentType");
            String content = response.getString("content");

            // TODO : Create Message and add it to where the f*ck it need to be added.
            Message message = new Message(datetime.toString(), senderId, text,
                    contentType, content);
            // TODO : (Nico) Notify activity.
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupCreateResponse(Response serverResponse) {
        try {
            // TODO : Change behaviour so that you don't go quit activity as long as group is not accepted.
            YieldsApplication.getUser().activateGroup(
                    new Id(serverResponse.getMessage().getLong("nid")));
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleUserInfoResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            Id infoId = new Id(response.getLong("uid"));
            User user = YieldsApplication.getUserFromId(infoId);
            if (response.getString("email").equals("")) {
                if (user != null) {
                    user.update(response);
                } else {
                    user = new User(response);
                    YieldsApplication.addNotKnown(user);
                }
            } else {
                if (YieldsApplication.getUser().getId().getId().equals(-1) ||
                        YieldsApplication.getUser().getId().equals(infoId)) {
                    YieldsApplication.getUser().update(response);

                    JSONArray entourage = response.getJSONArray("entourage");
                    JSONArray entourageRefreshedAt = response.getJSONArray("entourageUpdatedAt");

                    if (entourage != null && entourageRefreshedAt != null) {
                        for (int i = 0; i < entourage.length(); i++) {
                            // TODO : Improve this, add field in user  ?
                            ServiceRequest userInfoRequest = new UserInfoRequest(YieldsApplication.getUser(),
                                    new Id(entourage.getLong(i)));

                            mService.sendRequest(userInfoRequest);
                        }
                    }
                } else {
                    if (user == null) {
                        user = new User(response);
                        YieldsApplication.getUser().addUserToEntourage(user);
                    } else {
                        user.update(response);
                    }

                }
            }
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleUserGroupListResponse(Response serverResponse) {
        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray groupsId = response.getJSONArray("groups");
            JSONArray updatedAt = response.getJSONArray("updatedAt");
            JSONArray refreshedAt = response.getJSONArray("refreshedAt");

            int groupCount = groupsId.length();
            assert (groupCount == updatedAt.length() &&
                    groupCount == refreshedAt.length());

            for (int i = 0; i < groupCount; i++) {
                Group group = new Group(groupsId.getString(i), "placeholder", refreshedAt
                        .getString(i));
                YieldsApplication.getUser().addGroup(group);
                ServiceRequest groupInfo =
                        new GroupInfoRequest(YieldsApplication.getUser().getId(), group.getId());
                mService.sendRequest(groupInfo);
                ServiceRequest historyRequest = new NodeHistoryRequest(group.getId(), new Date());
                mService.sendRequest(historyRequest);
            }

            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

    }

    protected void handleUserConnectResponse(Response serverResponse) {
        // TODO : Parse the new field.
        ClientUser user = YieldsApplication.getUser();
        try {
            user.setId(new Id(serverResponse.getMessage().getLong("uid")));
            if (serverResponse.getMessage().getBoolean("returning")) {
                ServiceRequest userInfoRequest = new UserInfoRequest(user, user.getId());
                mService.sendRequest(userInfoRequest);
                mService.notifyChange(NotifiableActivity.Change.CONNECTED);
                ServiceRequest groupListRequest = new UserGroupListRequest(user);
                mService.sendRequest(groupListRequest);
            } else {
                mService.notifyChange(NotifiableActivity.Change.NEW_USER);
            }
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }


    /**
     * Handles NodeMessage responses.
     *
     * @param serverResponse The Response received from the server.
     */
    protected void handleNodeMessageResponse(Response serverResponse) {
        // TODO : Handle errors JSONs
        try {
            JSONObject responseMessage = serverResponse.getMessage();
            JSONObject metadata = serverResponse.getMetadata();
            String time = metadata.getString("ref");
            Date date = DateSerialization.dateSerializer.toDate(time);
            Group group = mCacheHelper.getGroup(new Id(Long.valueOf(metadata.getString("client"))));
            Message message = mCacheHelper.getMessagesForGroup(group.getId(), date, 1).get(0);
            mCacheHelper.deleteMessage(message, group.getId());
            Message updatedMessage = new Message("", new Id(-1), message.getSender(), message.getContent(),
                    DateSerialization.dateSerializer.toDate(metadata.getString("datetime")),
                    Message.MessageStatus.SENT);
            mCacheHelper.addMessage(updatedMessage, group.getId());
            mService.receiveMessage(group.getId(), updatedMessage);
        } catch (JSONException | ParseException | CacheDatabaseException e) {
            Log.d("Y:" + this.getClass().getName(), "Couldn't handle NodeMessageResponse correctly !");
        }
    }

    /**
     * Handles NodeHistory responses.
     *
     * @param serverResponse The response received from the server.
     */
    protected void handleNodeHistoryResponse(Response serverResponse) {
        try {
            long nid = serverResponse.getMessage().getLong("nid");
            JSONArray datetimes = serverResponse.getMessage().getJSONArray("datetimes");
            JSONArray senders = serverResponse.getMessage().getJSONArray("senders");
            JSONArray texts = serverResponse.getMessage().getJSONArray("texts");
            JSONArray contentTypes = serverResponse.getMessage().getJSONArray("contentTypes");
            JSONArray contents = serverResponse.getMessage().getJSONArray("contents");

            int count = datetimes.length();
            assert (count == senders.length() && count == texts.length() && count == contentTypes
                    .length() && count == contents.length());

            Id groupId = new Id(nid);
            ArrayList<Message> messageList = new ArrayList<>();
            for (int i = 0; i < count; i++) {

                Message message = new Message(datetimes.getString(i), senders.getLong(i), texts
                        .getString(i), contentTypes.getString(i), contents.getString(i));
                messageList.add(message);
                //mCacheHelper.addMessage(message, groupId);
            }

            mService.receiveMessages(groupId, messageList);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

}
