package yields.client.yieldsapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import yields.client.id.Id;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.service.YieldServiceBinder;

public class YieldsApplication {
    private static ClientUser mUser;
    private static Node mUserSearched;
    private static List<User> mUserList;
    private static List<User> mNotKnown = new ArrayList<>();
    private static Group mGroup;
    private static Group mInfoGroup;

    private static List<Group> mGroupsSearched = new ArrayList<>();
    private static Date mLastDateSearch;
    private static Group mGroupAdded;
    private static boolean mGroupAddedValid;

    private static Context mApplicationContext;
    private static Resources mResources;

    private static Bitmap mDefaultGroupImage = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
    private static Bitmap mDefaultUserImage = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);

    private static GoogleApiClient mGoogleApiClient;
    private static Toast mToast = null;

    private static YieldServiceBinder mBinder = null;

    private static Bitmap mShownImage;

    private static Bitmap mDefaultThumbnail;

    /**
     * Getter for the user of the application.
     *
     * @return The client user.
     */
    public static ClientUser getUser() {
        return mUser;
    }

    /**
     * Getter for the group currently displayed in the MessageActivity.
     *
     * @return This group.
     */
    public static Group getGroup() {
        return mGroup;
    }

    /**
     * Adds a User to the app which is not in the ClientUser's entourage.
     *
     * @param notKnown The User that is not known from the ClientUser (not in it's entourage).
     */
    public static void addNotKnown(User notKnown) {
        mNotKnown.add(notKnown);
    }

    /**
     * Getter for the list of groups currently displayed in the SearchGroupActivity.
     *
     * @return The list of groups displayed.
     */
    public static List<Group> getGroupsSearched() {
        return mGroupsSearched;
    }

    /**
     * Getter for the last time we searched for groups.
     *
     * @return The Date of the last search.
     */
    public static Date getLastDateSearch() {
        return mLastDateSearch;
    }

    /**
     * Setter for the last time we searched for groups.
     *
     * @param lastSearch The mLastDateSearch time.
     */
    public static void setLastDateSearch(Date lastSearch) {
        YieldsApplication.mLastDateSearch = lastSearch;
    }

    /**
     * Getter for the group which will be added in the new group.
     *
     * @return The group which will be added in the new group.
     */
    public static Group getGroupAdded() {
        return mGroupAdded;
    }

    /**
     * Getter indicating if the group which will be added is valid.
     *
     * @return True iff the group in getGroupAdded is valid.
     */
    public static boolean isGroupAddedValid() {
        return mGroupAddedValid;
    }

    /**
     * Getter for the list of users currently displayed in the UserListActivity.
     *
     * @return The list of users displayed.
     */
    public static List<User> getUserList() {
        return mUserList;
    }

    /**
     * Getter for the user currently displayed in the UserInfoActivity.
     *
     * @return The user displayed
     */
    public static Node getUserSearched() {
        return mUserSearched;
    }

    /**
     * Getter for the application context.
     *
     * @return The context of the application.
     */
    public static Context getApplicationContext() {
        return mApplicationContext;
    }

    /**
     * Getter the default group image used on group creation.
     *
     * @return The default group image.
     */
    public static Bitmap getDefaultGroupImage() {
        return mDefaultGroupImage;
    }

    /**
     * Getter the default user image.
     *
     * @return The default user image.
     */
    public static Bitmap getDefaultUserImage() {
        return mDefaultUserImage;
    }

    /**
     * Getter for the google api client of the App.
     *
     * @return The GoogleAPIClient
     */
    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    /**
     * Setter for the google api client.
     *
     * @param gac The google api client.
     */

    public static void setGoogleApiClient(GoogleApiClient gac) {
        Objects.requireNonNull(gac);

        mGoogleApiClient = gac;
    }

    /**
     * Setter for the resource of the app.
     *
     * @param r The resources to use.
     */

    public static void setResources(Resources r) {
        Objects.requireNonNull(r);
        mResources = r;
    }

    /**
     * Setter for the group to be displayed in the Message Activity.
     *
     * @param g The group.
     */

    public static void setGroup(Group g) {
        Objects.requireNonNull(g);
        mGroup = g;
    }

    /**
     * Setter for the group to be displayed in the Message Activity.
     *
     * @param g The group.
     */

    public static void setInfoGroup(Group g) {
        Objects.requireNonNull(g);
        mInfoGroup = g;
    }

    /**
     * Getter for the group to be displayed in the Message Activity.
     *
     * @return The group.
     */

    public static Group getInfoGroup() {
        return mInfoGroup;
    }

    public static void nullGroup() {
        mGroup = null;
    }

    /**
     * Setter for the client user of the app.
     *
     * @param u The client user to use.
     */

    public static void setUser(ClientUser u) {
        Objects.requireNonNull(u);
        mUser = u;
    }

    /**
     * Setter for the group list to be displayed in SearchGroupActivity
     *
     * @param groups The group.
     */

    public static void setGroupsSearched(List<Group> groups) {
        Objects.requireNonNull(groups);
        mGroupsSearched = Collections.unmodifiableList(Objects.requireNonNull(groups));
    }

    /**
     * Setter for the group to be added in the newly created group.
     *
     * @param g The group to be added.
     */
    public static void setGroupAdded(Group g) {
        mGroupAdded = Objects.requireNonNull(g);
    }

    /**
     * Setter indicating if the group to be added is valid.
     *
     * @param b Iff the added group is valid.
     */
    public static void setGroupAddedValid(boolean b) {
        mGroupAddedValid = b;
    }

    /**
     * Setter for the user list to be displayed in UserListActivity
     *
     * @param users The user list.
     */
    public static void setUserList(List<User> users) {
        mUserList = new ArrayList<>(Objects.requireNonNull(users));
    }

    /**
     * Setter for the user to be displayed in UserInfoActivity
     *
     * @param node The user that will be displayed.
     */
    public static void setUserSearched(Node node) {
        mUserSearched = Objects.requireNonNull(node);
    }

    /**
     * Setter for the application context.
     *
     * @param c The context to use.
     */
    public static void setApplicationContext(Context c) {
        Objects.requireNonNull(c);
        mApplicationContext = c;
    }

    /**
     * Setter for the group default image.
     *
     * @param b The default group image to use.
     */

    public static void setDefaultGroupImage(Bitmap b) {
        Objects.requireNonNull(b);
        mDefaultGroupImage = b;
    }

    /**
     * Setter for the user default image.
     *
     * @param b The default user image to use.
     */

    public static void setDefaultUserImage(Bitmap b) {
        Objects.requireNonNull(b);

        mDefaultUserImage = b;
    }

    /**
     * Getter for the ressources of the app.
     *
     * @return The application ressources.
     */
    public static Resources getResources() {
        return mResources;
    }

    /**
     * Gets the binder to the service.
     *
     * @return The sus-mentioned binder.
     */
    public static YieldServiceBinder getBinder() {
        return mBinder;
    }

    /**
     * Sets the binder to the service
     *
     * @param binder The sus-mentioned binder.
     */
    public static void setBinder(YieldServiceBinder binder) {
        Objects.requireNonNull(binder);
        mBinder = binder;
    }

    /**
     * nulls the binder to the service
     */
    public static void nullBinder() {
        mBinder = null;
    }

    /**
     * Static method used for centralized toast display
     *
     * @param context The app context
     * @param text    The message to write on the toast
     */
    public static void showToast(Context context, String text) {
        cancelToast();

        mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * Cancels the current displayed toast, if any
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    /**
     * Setter for the image to be displayed in the popup.
     *
     * @param image The image to be displayed in the popup.
     */
    public static void setShownImage(Bitmap image) {
        Objects.requireNonNull(image);
        mShownImage = image;
    }

    /**
     * Getter for the image to be displayed in the popup window.
     *
     * @return The image.
     */
    public static Bitmap getShownImage() {
        return mShownImage;
    }

    /**
     * Get a certain User.
     *
     * @param userId The id of the user needed.
     * @return The user to be modified.
     */
    public static User getUserFromId(Id userId) {
        if (userId.equals(mUser.getId())) {
            return mUser;
        }

        User user = mUser.getUserFromEntourage(userId);


        if (user == null) {
            for (User userN : mNotKnown) {
                if (userId.equals(userN.getId())) {
                    return userN;
                }
            }
        }

        return user;
    }

    /**
     * Get a certain User.
     *
     * @param nodeId The id of the user needed.
     * @return The user to be modified.
     */
    public static Node getNodeFromId(Id nodeId) {
        if (nodeId.equals(mUser.getId())) {
            return mUser;
        }

        Node node = mUser.getUserFromEntourage(nodeId);

        if (node == null) {
            node = mUser.getNodeFromId(nodeId);
        }

        if (node == null) {
            for (User userN : mNotKnown) {
                if (nodeId.equals(userN.getId())) {
                    return userN;
                }
            }
        }

        if (node == null) {
            return mUser.getGroup(nodeId);
        }

        return node;
    }

    /**
     * Setter for the default thumbnail.
     *
     * @param thumbnail The default thumbnail to use.
     */
    public static void setDefaultTheumbnail(Bitmap thumbnail) {
        mDefaultThumbnail = Objects.requireNonNull(thumbnail);
    }

    /**
     * Getter for the default thumbnail.
     *
     * @return the default thumbnail.
     */
    public static Bitmap getDefaultThumbnail() {
        return mDefaultThumbnail;
    }
}
