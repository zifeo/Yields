package yields.client.yieldsapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

import com.google.android.gms.common.api.GoogleApiClient;

import yields.client.node.ClientUser;
import yields.client.node.Group;

public class YieldsApplication {
    private static ClientUser mUser;
    private static Group mGroup;

    private static Context mApplicationContext;
    private static Resources mResources;

    private static Bitmap mDefaultGroupImage = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
    private static Bitmap mDefaultUserImage = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);

    private static GoogleApiClient mGoogleApiClient;

    /**
     * Getter for the user of the application.
     * @return The client user.
     */
    public static ClientUser getUser(){
        return mUser;
    }

    /**
     * Getter for the group currently displayed in the MessageActivity.
     * @return This group.
     */
    public static Group getGroup(){
        return mGroup;
    }

    /**
     * Getter for the application context.
     * @return The context of the application.
     */
    public static Context getApplicationContext(){
        return mApplicationContext;
    }

    /**
     * Getter the default group image used on group creation.
     * @return The default group image.
     */
    public static Bitmap getDefaultGroupImage(){
        return mDefaultGroupImage;
    }

    /**
     * Getter the default user image.
     * @return The default user image.
     */
    public static Bitmap getDefaultUserImage(){
        return mDefaultUserImage;
    }

    /**
     * Getter for the google api client of the App.
     * @return The GoogleAPIClient
     */
    public static GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    /**
     * Setter for the google api client.
     * @param gac The google api client.
     */
    public static void setGoogleApiClient(GoogleApiClient gac){
        mGoogleApiClient = gac;
    }

    /**
     * Setter for the ressources of the app.
     * @param r The ressources to use.
     */
    public static void setResources(Resources r){
        mResources = r;
    }

    /**
     * Setter for the group to be displayed in the Message Activity.
     * @param g The group.
     */
    public static void setGroup(Group g){
        mGroup = g;
    }

    /**
     * Setter for the client user of the app.
     * @param u The client user to use.
     */
    public static void setUser(ClientUser u){
        mUser = u;
    }

    /**
     * Setter for the application context.
     * @param c The context to use.
     */
    public static void setApplicationContext(Context c){
        mApplicationContext = c;
    }

    /**
     * Setter for the group default image.
     * @param b The default group image to use.
     */
    public static void setDefaultGroupImage(Bitmap b){
        mDefaultGroupImage = b;
    }

    /**
     * Setter for the user default image.
     * @param b The default user image to use.
     */
    public static void setDefaultUserImage(Bitmap b){
        mDefaultUserImage = b;
    }

    /**
     * Getter for the ressources of the app.
     * @return The application ressources.
     */
    public static Resources getResources(){
        return mResources;
    }
}
