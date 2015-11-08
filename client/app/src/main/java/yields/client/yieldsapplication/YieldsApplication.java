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

    public static ClientUser getUser(){
        return mUser;
    }

    public static Group getGroup(){
        return mGroup;
    }

    public static Context getApplicationContext(){
        return mApplicationContext;
    }

    public static Bitmap getDefaultGroupImage(){
        return mDefaultGroupImage;
    }

    public static Bitmap getDefaultUserImage(){
        return mDefaultUserImage;
    }

    public static GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    public static void setGoogleApiClient(GoogleApiClient gac){
        mGoogleApiClient = gac;
    }

    public static void setResources(Resources r){
        mResources = r;
    }

    public static void setGroup(Group g){
        mGroup = g;
    }

    public static void setUser(ClientUser u){
        mUser = u;
    }

    public static void setApplicationContext(Context c){
        mApplicationContext = c;
    }

    public static void setDefaultGroupImage(Bitmap b){
        mDefaultGroupImage = b;
    }

    public static void setDefaultUserImage(Bitmap b){
        mDefaultUserImage = b;
    }

    public static Resources getResources(){
        return mResources;
    }
}
