package yields.client.service;

import android.os.Binder;
import android.util.Log;

import java.util.Objects;

import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.RequestBuilder;

public class MessageBinder extends Binder {
    private final YieldService mService;

    public MessageBinder(YieldService service) {
        mService = service;
    }

    public void sendMessage(Group group, Message message){
        Request groupMessageReq = createRequestForMessageToSend(group, message);
        Log.d("REQUEST", "sendMessage = " + groupMessageReq.message());
        mService.sendRequest(groupMessageReq);
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
}
