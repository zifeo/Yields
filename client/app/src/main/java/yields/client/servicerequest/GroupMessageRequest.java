package yields.client.servicerequest;

import android.app.Service;

import java.util.Objects;

import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to add a Message to a Group.
 */
public class GroupMessageRequest extends ServiceRequest {

    private final Message mMessage;
    private final Group mReceivingGroup;

    /**
     * Main constructor for this type of ServiceRequest (sending a Message to a Group).
     *
     * @param message        The Message that should be sent.
     * @param receivingGroup The Group to which the Message should be added.
     */
    public GroupMessageRequest(Message message, Group receivingGroup) {
        super();
        Objects.requireNonNull(message);
        Objects.requireNonNull(receivingGroup);

        mMessage = message;
        mReceivingGroup = receivingGroup;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUP_MESSAGE;
    }

    /**
     * Build a ServerRequest for sending a message to a group.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.<<
     */
    @Override
    public ServerRequest parseRequestForServer() {
        Group group = getReceivingGroup();
        Message message = getMessage();

        return RequestBuilder.groupMessageRequest(message.getSender().getId(), group.getId(),
                message.getContent());
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
}
