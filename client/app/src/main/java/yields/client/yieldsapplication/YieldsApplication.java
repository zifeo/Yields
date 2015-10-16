package yields.client.yieldsapplication;

import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;

public class YieldsApplication {
    private static ClientUser mUser;
    private static Group mGroup;

    public static ClientUser getUser(){
        return mUser;
    }

    public static Group getGroup(){
        return mGroup;
    }

    public static void setGroup(Group g){
        mGroup = g;
    }

    public static void setUser(ClientUser u){
        mUser = u;
    }
}
