package yields.client.service;

import android.os.Binder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import yields.client.activities.MessageActivity;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;

public class MessageBinder extends Binder {
    private final YieldService mService;
    private boolean linked;

    /**
     * Creates the binder and links it to the service
     * @param service The Service concerned
     */
    public MessageBinder(YieldService service) {
        mService = service;
    }

    /**
     * Binds the activity to the service which permits it to receive incoming messages directly
     * @param activity the current messagingActivity
     */
    public void bind(MessageActivity activity) {
        Objects.requireNonNull(activity);
        mService.setMessageActivity(activity);
    }

    /**
     * Sends a message to the server
     * @param group The group to which is linked the message
     * @param message The message itself
     * @throws IOException in case of communication errors
     */
    public void sendMessage(Group group, Message message){
        Request groupMessageReq = createRequestForMessageToSend(group, message);
        Log.d("REQUEST", "sendMessage = " + groupMessageReq.message());
        mService.sendRequest(groupMessageReq);
    }

    public void addMoreGroupMessages(Group group,
                                 Date lastDate)
            throws IOException {
        final int MESSAGE_COUNT = 20;

        Request groupHistoryRequest = RequestBuilder
                .GroupHistoryRequest(group.getId(), lastDate, MESSAGE_COUNT);
        mService.sendRequest(groupHistoryRequest);
        Log.d("REQUEST", "getGroupMessages " + groupHistoryRequest.message());
    }

    private static Request createRequestForMessageToSend(Group group, Message message){
        Objects.requireNonNull(group);
        Objects.requireNonNull(message);
        Request req;
        switch (message.getContent().getType()) {
            case "image":
                req = RequestBuilder
                        .GroupImageMessageRequest(message.getSender().getId(), group.getId(),
                                "text", (ImageContent) message.getContent());
                break;
            case "text":
                req = RequestBuilder
                        .GroupTextMessageRequest(message.getSender().getId(), group.getId(),
                                "text", ((TextContent) message
                                        .getContent()).getText());
                break;
            default : throw new IllegalArgumentException("type unknown");
        }
        return req;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mService.unsetMessageActivity();
    }
}
