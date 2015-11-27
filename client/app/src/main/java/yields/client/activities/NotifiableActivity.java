package yields.client.activities;

import android.support.v7.app.AppCompatActivity;

import yields.client.yieldsapplication.YieldsApplication;

/**
 * Small interface that requires to implement a
 * notifyChange() method, which tells the activity
 * that the data set it holds has changed
 */
public abstract class NotifiableActivity extends AppCompatActivity{

    public enum Change {
        GROUP_SEARCH, MESSAGES_RECEIVE, GROUP_LIST, GROUP_LEAVE, GROUP_JOIN
    }

    /**
     * Automatically called when the activity is resumed after another
     * activity  was displayed
     */
    @Override
    public void onResume(){
        super.onResume();
        YieldsApplication.getBinder().attachActivity(this);
        YieldsApplication.getBinder().connectionStatus();
    }

    /**
     * Called to pause the activity
     */
    @Override
    public void onPause(){
        super.onPause();
        YieldsApplication.getBinder().unsetMessageActivity();
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
