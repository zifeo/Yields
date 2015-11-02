package yields.client.gui;

import yields.client.node.User;

/**
 * Class used in lists of users, to indicate if the user is selected
 */
public class PairUserBoolean {
    private User mUser;
    private boolean mBoolean;

    public PairUserBoolean(User user, boolean b){
        mUser = user;
        mBoolean = b;
    }

    public User getUser(){
        return mUser;
    }

    public void setBoolean(boolean b){
        mBoolean = b;
    }

    public boolean getBoolean(){
        return mBoolean;
    }
}