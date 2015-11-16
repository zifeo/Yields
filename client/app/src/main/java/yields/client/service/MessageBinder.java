package yields.client.service;

import android.os.Binder;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import yields.client.activities.MessageActivity;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.serverconnection.ServerRequest;
import yields.client.serverconnection.RequestBuilder;

public class MessageBinder extends Binder {
    private final YieldService mService;

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
    public void attachActivity(MessageActivity activity) {
        Objects.requireNonNull(activity);
        mService.setNotifiableActivity(activity);
    }

    /**
     * Can be used to know if the server is connected to the server
     */
    public boolean isServerConnected(){
        //TODO implement a mean of knowing if the server is connected
        return true;
    }

    /**
     * Sends a message to the server
     * @param group The group to which is linked the message
     * @param message The message itself
     * @throws IOException in case of communication errors
     */
    public void sendMessage(Group group, Message message){
        ServerRequest groupMessageReq = createRequestForMessageToSend(group, message);
        Log.d("REQUEST", "sendMessage = " + groupMessageReq.message());
        mService.sendRequest(groupMessageReq);
    }

    /**
     * Add older message to the Message Activity
     *
     * @param group The group we want to retrieve from
     * @param lastDate The last date we have in the history
     * @param messageCount The max number of message we want
     */
    public void addMoreGroupMessages(Group group,
                                 Date lastDate, int messageCount) {
        /*ServerRequest groupHistoryServerRequest = RequestBuilder
                .GroupHistoryRequest(group.getId(), lastDate, messageCount);*/
        //TODO : Stuff
        //mService.sendRequest(groupHistoryServerRequest);
        //Log.d("REQUEST", "getGroupMessages " + groupHistoryServerRequest.message());
    }

    /**
     * Build a request for sending a message to a group.
     * @param group The group receiving the message.
     * @param message The message to be sent to the group.
     * @return The request.
     */
    private static ServerRequest createRequestForMessageToSend(Group group, Message message){
        Objects.requireNonNull(group);
        Objects.requireNonNull(message);
        return RequestBuilder.groupMessageRequest(message.getSender().getId(), group.getId(),
                message
               .getContent());
    }

    /**
     * Finalize the binder.
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mService.unsetMessageActivity();
    }
}
