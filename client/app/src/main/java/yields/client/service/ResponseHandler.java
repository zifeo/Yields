package yields.client.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.serverconnection.Response;
import yields.client.servicerequest.GroupHistoryRequest;
import yields.client.servicerequest.GroupInfoRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserEntourageAddRequest;
import yields.client.servicerequest.UserGroupListRequest;
import yields.client.servicerequest.UserInfoRequest;
import yields.client.yieldsapplication.YieldsApplication;

public class ResponseHandler {

    private final YieldService mService;
    private final CacheDatabaseHelper mCacheHelper;

    public ResponseHandler(CacheDatabaseHelper cacheDatabaseHelper, YieldService yieldService) {
        mCacheHelper = cacheDatabaseHelper;
        mService = yieldService;
    }


    protected void handleUserUpdateResponse(Response serverResponse){
        Log.d("Y:" + this.getClass().getName(), "Response for UserUpdate");
        // Nothing to parse.
        // TODO : decide what to do.
    }

    /**
     * Seriously fuck Java.
     * @param array The array to downgrade.
     * @return The converted array.
     */
    protected byte[] convertToPrimitiveByteArray(Byte[] array){
        byte[] convertedArray  = new byte[array.length];
        for (int i = 0 ; i < convertedArray.length ; i ++){
            convertedArray[i] = array[i];
        }
        return convertedArray;
    }

    protected void handleUserSearchResponse(Response serverResponse){
        try {
            JSONObject response = serverResponse.getMessage();
            long uid = response.getLong("uid");
            if (uid != 0) {
                Id id = new Id(uid);
                ServiceRequest addToEntourageRequest =
                        new UserEntourageAddRequest(YieldsApplication.getUser().getId(), id);
                mService.sendRequest(addToEntourageRequest);
                YieldsApplication.getUser().addUserToEntourage(new User(id));
            } else {
                mService.notifyChange(NotifiableActivity.Change.NOT_EXIST);
            }


        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleNodeSearchResponse(Response serverResponse){
        try {
            JSONObject response = serverResponse.getMessage();
            JSONArray nodes = response.getJSONArray("nodes");
            JSONArray names = response.getJSONArray("names");
            String pics = response.getString("pic");

            assert (nodes.length() == names.length() && nodes.length() == pics.length());
            int nodeCount = nodes.length();
            ArrayList<Node> nodeList = new ArrayList<>();
            for (int i = 0 ; i < nodeCount ; i ++){
                long nid = nodes.getLong(i);
                Id id = new Id(nid);
                String nodeName = names.getString(i);
                byte[] pic = Base64.decode(pics, Base64.DEFAULT);

                new Group(nodeName, id, new ArrayList<User>(), BitmapFactory.decodeByteArray(pic, 0, pic.length));
            }
            // TODO : (Nico) Notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupUpdateResponse(Response serverResponse){
        Log.d("ServiceRequestCtrll", "Response for Group Update.");
        // No output.
        // TODO : decide what to do.
    }

    protected void handleGroupInfoResponse(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<User> userList = new ArrayList<>();
            for (int i = 0 ; i < users.length() ; i ++) {
                if (!(new Id(users.getLong(i))).equals(YieldsApplication.getUser().getId())) {
                    User user = new User("", new Id(users.getLong(i)), "", YieldsApplication
                            .getDefaultUserImage());
                    userList.add(user);
                    ServiceRequest userInfoRequest = new UserInfoRequest(YieldsApplication.getUser(),
                            new Id(users.getLong(i)));
                    mService.sendRequest(userInfoRequest);
                }

                //TODO : Add nodes field to group.
            }

            // _KetzA : I'm not really sure what to do here ...
            Group group = YieldsApplication.getUser().modifyGroup(new Id(nid));
            group.setName(name);
            mService.notifyChange(NotifiableActivity.Change.GROUP_LIST);
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupMessageResponse(Response serverResponse){
        try{
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

    protected void handlePublisherCreateResponse(Response serverResponse){
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Id publisherId = new Id(nid);

            // TODO : (Nico) notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handlePublisherUpdateResponse(Response serverResponse){
        Log.d("ServiceRequestCtrllr", "Response for Publisher Update.");
        // Nothing to parse.
        // TODO : decide what to do.
    }

    protected void handlePublisherInfoResponse(Response serverResponse){
        try {
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Id id = new Id(nid);
            String name = response.getString("name");
            Byte[] pic = (Byte[]) response.get("pic");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            // TODO : Create the instance when declared.
            // TODO : (Nico) Notifiy activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handlePublisherMessageResponse(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Date datetime = DateSerialization.dateSerializer.toDate(response.getString("datetime"));
            Id id = new Id(nid);

            // TODO : (Nico) Notify Activtiy .
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleRSSCreateResponse(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            Id id = new Id(nid);

            // TODO (Nico) : Notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleNodeMessageBroadcast(Response serverResponse){
        // TODO : Not very clear on how to use this. See later.
        try{
            JSONObject response = serverResponse.getMessage();
            String kind = response.getString("kind");
            JSONObject message = response.getJSONObject("message");
            // TODO : What to do with message ?
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleUserUpdateBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long uid = response.getLong("uid");
            String name = response.getString("name");
            byte[] pic = convertToPrimitiveByteArray((Byte[]) response.get("pic"));

            User updatedUser = new User(name, new Id(uid), "", BitmapFactory.decodeByteArray(pic,
                    0, pic.length));

            // TODO : (Nico) Notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupCreateBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<User> userList = new ArrayList<>();
            for (int i = 0 ; i < users.length() ; i ++){
                userList.add(new User("", new Id(users.getLong(i)), "", YieldsApplication
                        .getDefaultUserImage()));
                ServiceRequest userInfoRequest = new UserInfoRequest(YieldsApplication.getUser(),
                        new Id(users.getLong(i)));
                mService.sendRequest(userInfoRequest);

            }

            Group newGroup = new Group(name, new Id(nid), userList);

            // TODO : (Nico) Notify app.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupUpdateBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            byte[] pic = convertToPrimitiveByteArray((Byte[]) response.get("pic"));
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            ArrayList<User> userList = new ArrayList<>();
            for (int i = 0 ; i < users.length() ; i ++){
                userList.add(new User("", new Id(users.getLong(i)), "", YieldsApplication
                        .getDefaultUserImage()));
                // TODO : Send userInfos for each user ???
            }

            Group updatedGroup = new Group(name, new Id(nid), userList, BitmapFactory
                    .decodeByteArray(pic, 0, pic.length));

            // TODO : (Nico) Notify app.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handleGroupMessageBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();

            long nid = response.getLong("nid");
            Date datetime = DateSerialization.dateSerializer.toDate(response.getString("datetime"));
            long senderId = response.getLong("sender");
            String text = response.getString("text");
            String contentType = response.getString("contentType");
            String content = response.getString("content");

            Message message = new Message(datetime.toString(), senderId, text,
                    contentType, content);
            // TODO : (Nico) Notify activity.
        } catch (JSONException | ParseException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handlePublisherCreateBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            // TODO : Create instance (Not yet declared topkek)
            // TODO : (Nico) notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handlePublisherUpdateBroadcast(Response serverResponse){
        try{
            JSONObject response = serverResponse.getMessage();
            long nid = response.getLong("nid");
            String name = response.getString("name");
            Byte[] pic = (Byte[]) response.get("pic");
            JSONArray users = response.getJSONArray("users");
            JSONArray nodes = response.getJSONArray("nodes");

            // TODO : Update instance (Not yet declared topkek)
            // TODO : (Nico) notify activity.
        } catch (JSONException e) {
            Log.d("Y:" + this.getClass().getName(), "failed to parse response : " +
                    serverResponse.object().toString());
        }
    }

    protected void handlePublisherMessageBroadcast(Response serverResponse){
        try{
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

    protected void handleRSSCreateBroadcast(Response serverResponse){
        try{
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

    protected void handleRSSMessageBroadcast(Response serverResponse){
        try{
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
            if (YieldsApplication.getUser().getId().getId().equals(-1) ||
                    YieldsApplication.getUser().getId().getId().equals(response.getLong("uid"))){
                YieldsApplication.getUser().update(response);
                JSONArray entourage = response.getJSONArray("entourage");
                JSONArray entourageRefreshedAt = response.getJSONArray("entourageUpdatedAt");

                if (entourage != null && entourageRefreshedAt != null){
                    for (int i = 0; i < entourage.length(); i++) {
                        // TODO : Improve this, add field in user  ?
                        if (DateSerialization.dateSerializer.toDate(entourageRefreshedAt.getString(i)
                        ).compareTo(new Date()) == -1){
                            ServiceRequest userInfoRequest = new UserInfoRequest(YieldsApplication.getUser(),

                                    new Id(entourage.getLong(i)));
                            mService.sendRequest(userInfoRequest);
                        }
                    }
                }
            }
            else {
                User user = YieldsApplication.getUser(new Id(response.getLong("uid")));
                user.setName(response.getString("name"));
                user.setEmail(response.getString("email"));


                byte[] byteArray = Base64.decode(response.getString("pic"), Base64.DEFAULT);
                Bitmap img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                user.setImg(img);

            }
        } catch (JSONException | ParseException e) {
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

            for (int i = 0 ; i < groupCount ; i ++){
                Group group = new Group(groupsId.getString(i), "placeholder", refreshedAt
                        .getString(i));
                YieldsApplication.getUser().addGroup(group);
                ServiceRequest groupInfo =
                        new GroupInfoRequest(YieldsApplication.getUser().getId(),group.getId());
                mService.sendRequest(groupInfo);
                ServiceRequest historyRequest = new GroupHistoryRequest(group, new Date());
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
                ServiceRequest groupListRequest = new UserGroupListRequest(user);
                mService.sendRequest(groupListRequest);
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
            Message message = mCacheHelper.getMessagesForGroup(group, date, 1).get(0);
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
            for (int i = 0 ; i < count ; i ++) {

                Log.d("AFHIUDGSFG AS", contentTypes.getString(i));

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
