package yields.client.servicerequest;

import android.app.Service;
import android.graphics.Bitmap;

import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.Response;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to rename a Group.
 */
public class GroupUpdateImageRequest extends ServiceRequest {

    private final User mSender;
    private final Group mGroup;
    private final Bitmap mImage;

    /**
     * Main constructor for this type of ServiceRequest (updating a Group's image).
     *
     * @param sender The User that created this request.
     * @param group  The Group that should have it's image updated.
     * @param image  The new image for Group.
     */
    public GroupUpdateImageRequest(User sender, Group group, Bitmap image) {
        super();
        mSender = sender;
        mGroup = group;
        mImage = image.copy(image.getConfig(), true);
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUPUPDATEIMAGE;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.groupUpdateImageRequest(mSender.getId(), mGroup.getId(), mImage);
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {
        //TODO : @Trofleb
    }
}
