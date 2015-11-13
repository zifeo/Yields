package yields.client.servicerequest;

import android.app.Service;

import java.util.Objects;

import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.yieldsapplication.YieldsApplication;

public class AddMessageRequest extends ServiceRequest{

    Message mMessage;
    Group mReceivingGroup;

    public AddMessageRequest(Message message, Group receivingGroup){
        Objects.requireNonNull(message);
        Objects.requireNonNull(receivingGroup);
        mMessage = message;
        mReceivingGroup = receivingGroup;
    }

    @Override
    public String getType(){
        return "AddMessage";
    }

    @Override
    public Request parseRequestForServer(){
        return createRequestForMessageToSend(mReceivingGroup,mMessage);
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {

    }

    public Message getMessage(){
        return mMessage;
    }

    public Group getReceivingGroup(){
        return mReceivingGroup;
    }

    /**
     * Build a request for sending a message to a group.
     * @param group The group receiving the message.
     * @param message The message to be sent to the group.
     * @return The request.
     */
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
