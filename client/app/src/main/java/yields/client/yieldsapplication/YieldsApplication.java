package yields.client.yieldsapplication;

import yields.client.node.Group;
import yields.client.node.User;

public class YieldsApplication {
    private static User mUser;
    private static Group mGroup;

    public static User getUser(){
        return mUser;
    }

    public static Group getGroup(){
        return mGroup;
    }

    public static void setGroup(Group g){
        mGroup = g;
    }

    public static void setUser(User u){
        mUser = u;
    }
}
