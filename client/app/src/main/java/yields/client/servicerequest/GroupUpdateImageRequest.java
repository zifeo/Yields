package yields.client.servicerequest;

import android.graphics.Bitmap;

import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

/**
 * ServerRequest asking the Service to rename a Group.
 */
public class GroupUpdateImageRequest extends ServiceRequest {

    private final User mSender;
    private final Id mGroupId;
    private final Bitmap mImage;
    private final Group.GroupType mType;

    /**
     * Main constructor for this type of ServiceRequest (updating a Group's image).
     *
     * @param sender  The User that created this request.
     * @param groupId The Group that should have it's image updated.
     * @param image   The new image for Group.
     */
    public GroupUpdateImageRequest(User sender, Id groupId, Bitmap image,
                                   Group.GroupType groupType) {
        super();
        Objects.requireNonNull(sender);
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(image);

        mSender = sender;
        mGroupId = groupId;
        mImage = image.copy(image.getConfig(), false);
        mType = groupType;
    }

    /**
     * Returns the type of this ServiceRequest.
     *
     * @return The type of this ServiceRequest.
     */
    @Override
    public RequestKind getType() {
        return RequestKind.GROUP_UPDATE_IMAGE;
    }

    /**
     * Build a ServerRequest for this ServiceRequest.
     *
     * @return The ServerRequest corresponding to this ServiceRequest.
     */
    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.groupUpdateImageRequest(mSender.getId(), mGroupId,
                mImage, mType);
    }

    /**
     * Returns the new Bitmap image of the Group.
     *
     * @return The new new Bitmap image of the Group.
     */
    public Bitmap getNewGroupImage() {
        return mImage;
    }

    /**
     * Returns the Id of the Group that will have it's image updated.
     *
     * @return The Id of the Group that will have it's image updated.
     */
    public Id getGroupId() {
        return mGroupId;
    }
}
