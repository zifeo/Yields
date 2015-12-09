package yields.client.servicerequest;

import java.util.Objects;

import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to add a Message to a Group.
 */
public class GroupMessageRequest extends ServiceRequest {

    private final Message mMessage;
    private final Id mReceivingGroupId;
    private final Group.GroupType mType;

    /**
     * Main constructor for this type of ServiceRequest (sending a Message to a Group).
     *
     * @param message          The Message that should be sent.
     * @param receivingGroupId The Id of the Group to which the Message should be added.
     */
    public GroupMessageRequest(Message message, Id receivingGroupId, Group.GroupType type) {
        super();
        Objects.requireNonNull(message);
        Objects.requireNonNull(receivingGroupId);
        Objects.requireNonNull(type);

        mMessage = message;
        mReceivingGroupId = receivingGroupId;
        mType = type;
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
     * Build a ServerRequest for sending a message to a Group.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.<<
     */
    @Override
    public ServerRequest parseRequestForServer() {
        Message message = getMessage();

        return RequestBuilder.groupMessageRequest(message.getSender(), mReceivingGroupId,
                mType, message.getContent(), message.getDate());
    }

    /**
     * Getter method for the Message of this ServiceRequest.
     *
     * @return The Message of this ServiceRequest.
     */
    public Message getMessage() {
        return mMessage;
    }

    /**
     * Getter method for the receiving Group of this ServiceRequest.
     *
     * @return The receiving Node of this ServiceRequest.
     */
    public Id getReceivingNodeId() {
        return mReceivingGroupId;
    }
}
