package yields.client.yieldsapplication;

import android.content.Context;

import java.util.ConcurrentModificationException;

import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;

public class YieldsApplication {
    private static ClientUser mUser;
    private static Group mGroup;
    private static Context mApplicationContext = getApplicationContext();

    public static ClientUser getUser(){
        return mUser;
    }

    public static Group getGroup(){
        return mGroup;
    }

    public static Context getApplicationContext(){
        return mApplicationContext;
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
}
