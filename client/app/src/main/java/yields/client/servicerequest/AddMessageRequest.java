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

/**
 * Request asking the Service to add a Message to a Group.
 */
public class AddMessageRequest extends ServiceRequest {

    private final Message mMessage;
    private final Group mReceivingGroup;

    /**
     * Main constructor for this type of ServiceRequest (sending a Message to a Group).
     *
     * @param message        The Message that should be sent.
     * @param receivingGroup The Group to which the Message should be added.
     */
    public AddMessageRequest(Message message, Group receivingGroup) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(receivingGroup);
        mMessage = message;
        mReceivingGroup = receivingGroup;
    }

    /**
     * Returns the type of this ServiceRequest as a String.
     *
     * @return The type of this ServiceRequest as a String.
     */
    @Override
    public String getType(){
        return "AddMessage";
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {

    }

    /**
     * Getter method for the Message of this ServiceRequest.
     *
     * @return The Message of this ServiceRequest.
     */
    public Message getMessage(){
        return mMessage;
    }

    /**
     * Getter method for the receiving Group of this ServiceRequest.
     *
     * @return The receiving Group of this ServiceRequest.
     */
    public Group getReceivingGroup(){
        return mReceivingGroup;
    }

    /**
     * Build a Request for sending a message to a group.
     *
     * @return The Request corresponding to this ServiceRequest.
     */
    @Override
    public Request parseRequestForServer() {
        Group group = getReceivingGroup();
        Message message = getMessage();
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
            default:
                throw new IllegalArgumentException("Message type not found !");
        }
        return req;
    }
}
