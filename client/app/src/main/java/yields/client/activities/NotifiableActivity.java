package yields.client.activities;

import android.support.v7.app.AppCompatActivity;

import yields.client.yieldsapplication.YieldsApplication;

/**
 * Small interface that requires to implement a
 * notifyChange() method, which tells the activity
 * that the data set it holds has changed
 */
public abstract class NotifiableActivity extends AppCompatActivity{

    /**
     * Represents the changes that can be done to the model and need
     * to be processed by the notified activity.
     */
    public enum Change {
        GROUP_SEARCH, MESSAGES_RECEIVE, GROUP_LIST, GROUP_LEAVE, GROUP_JOIN,
        CONNECTED, NEW_USER, NOT_EXIST, ADD_ENTOURAGE, ENTOURAGE_UPDATE, RSS_FAIL, RSS_CREATE
    }

    /**
     * Automatically called when the activity is resumed after another
     * activity was displayed.
     */
    @Override
    public void onResume(){
        super.onResume();
        if (YieldsApplication.getBinder() != null) {
            YieldsApplication.getBinder().attachActivity(this);
            YieldsApplication.getBinder().connectionStatus();
        }
    }

    /**
     * Called when the activity is paused.
     */
    @Override
    public void onPause(){
        super.onPause();
        if (YieldsApplication.getBinder() != null) {
            YieldsApplication.getBinder().unsetNotifiableActivity();
        }
    }

    /**
     * Method that tells the activity
     * that the data set it holds has changed
     */
    abstract public void notifyChange(Change changed);

    /**
     * Method called when the server is connected
     */
    abstract public void notifyOnServerConnected();

    /**
     * Method called when the server is disconnected
     */
    abstract public void notifyOnServerDisconnected();
}
