package yields.client.servicerequest;

import java.util.Objects;

import yields.client.messages.Message;
import yields.client.node.Node;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to add a Message to a Node.
 */
public class NodeMessageRequest extends ServiceRequest {

    private final Message mMessage;
    private final Node mReceivingNode;

    /**
     * Main constructor for this type of ServiceRequest (sending a Message to a Node).
     *
     * @param message        The Message that should be sent.
     * @param receivingNode The Node to which the Message should be added.
     */
    public NodeMessageRequest(Message message, Node receivingNode) {
        super();
        Objects.requireNonNull(message);
        Objects.requireNonNull(receivingNode);

        mMessage = message;
        mReceivingNode = receivingNode;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.NODE_MESSAGE;
    }

    /**
     * Build a ServerRequest for sending a message to a node.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.<<
     */
    @Override
    public ServerRequest parseRequestForServer() {
        Node group = getReceivingNode();
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
     * Getter method for the receiving Node of this ServiceRequest.
     *
     * @return The receiving Node of this ServiceRequest.
     */
    public Node getReceivingNode(){
        return mReceivingNode;
    }
}
