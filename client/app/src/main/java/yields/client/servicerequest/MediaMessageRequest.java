package yields.client.servicerequest;

import java.util.Objects;

import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to add a Message to a Media.
 */
public class MediaMessageRequest extends ServiceRequest {

    private final Message mMessage;
    private final Id mReceivingNodeId;
    private final Group.GroupType mType;

    /**
     * Main constructor for this type of ServiceRequest (sending a Message to a Media).
     *
     * @param message         The Message that should be sent.
     * @param receivingNodeId The Id of the Media to which the Message should be added.
     */
    public MediaMessageRequest(Message message, Id receivingNodeId, Group.GroupType visibility) {
        super();
        Objects.requireNonNull(message);
        Objects.requireNonNull(receivingNodeId);
        Objects.requireNonNull(visibility);

        mMessage = message;
        mReceivingNodeId = receivingNodeId;
        mType = visibility;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.MEDIA_MESSAGE;
    }

    /**
     * Build a ServerRequest for sending a message to a node.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.<<
     */
    @Override
    public ServerRequest parseRequestForServer() {
        Message message = getMessage();

        return RequestBuilder.groupMessageRequest(message.getSender(), mReceivingNodeId,
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
     * Getter method for the receiving Node of this ServiceRequest.
     *
     * @return The receiving Node of this ServiceRequest.
     */
    public Id getReceivingNodeId() {
        return mReceivingNodeId;
    }
}

