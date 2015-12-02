package yields.client.servicerequest;

import android.opengl.Visibility;
import android.util.Log;

import java.util.Objects;

import yields.client.id.Id;
import yields.client.messages.Message;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServiceRequest asking the Service to add a Message to a Node.
 */
public class NodeMessageRequest extends ServiceRequest {

    private final Message mMessage;
    private final Id mReceivingGroupId;
    private final Group.GroupVisibility mVisibility;


    /**
     * Main constructor for this type of ServiceRequest (sending a Message to a Node).
     *
     * @param message           The Message that should be sent.
     * @param receivingGroupId  The Id of the group to which the Message should be added.
     */
    public NodeMessageRequest(Message message, Id receivingGroupId,
                              Group.GroupVisibility visibility) {
        super();
        Objects.requireNonNull(message);
        Objects.requireNonNull(receivingGroupId);
        Objects.requireNonNull(visibility);

        mMessage = message;
        mReceivingGroupId = receivingGroupId;
        mVisibility = visibility;
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
        Message message = getMessage();

        Log.d("hello", "yellow");

        return RequestBuilder.nodeMessageRequest(message.getSender(), mReceivingGroupId,
                mVisibility, message.getContent(), message.getDate());
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
    public Id getReceivingNodeId(){
        return mReceivingGroupId;
    }
}
