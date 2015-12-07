package yields.client.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.plus.Plus;

import yields.client.R;
import yields.client.service.YieldService;
import yields.client.service.YieldServiceBinder;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserConnectRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity which is displayed until a response from
 * the server is received, indicating that the user
 * is now connected.
 */
public class LoggingInActivity extends NotifiableActivity {

    private boolean wasConnected = false;

    /**
     * onCreate method for the LoggingInActivity.
     * @param savedInstanceState The bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging_in);

        YieldsApplication.setApplicationContext(getApplicationContext());

        Intent serviceIntent = new Intent(this, YieldService.class)
                .putExtra("email", Plus.AccountApi
                        .getAccountName(YieldsApplication.getGoogleApiClient()));
        startService(serviceIntent);

        Intent serviceBindingIntent = new Intent(this, YieldService.class)
                .putExtra("bindGroupActivity", true);

        bindService(serviceBindingIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Method called by clientUser when the server indicates that the account
     * already exists.
     */
    public void goToGroupActivity(){
        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }

    /**
     * Method called by clientUser when the server indicates that the account
     * doesn't exist.
     */
    public void goToSelectUsernameActivity(){
        Intent intent = new Intent(this, SelectUsernameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (YieldsApplication.getBinder() != null) {
            unbindService(mConnection);
            YieldsApplication.nullBinder();
        }
    }

    /**
     * Method call when the connection response has been received
     *
     * @param changed What has changed
     */
    @Override
    public void notifyChange(Change changed) {
        switch (changed) {
            case NEW_USER:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goToSelectUsernameActivity();
                    }
                });
                break;
            case CONNECTED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goToGroupActivity();
                    }
                });
                break;
            default:
                Log.d("Y:" + this.getClass().getName(), "useless notify change...");
        }
    }

    /**
     * What happens when the server is connected
     */
    @Override
    synchronized public void notifyOnServerConnected() {
        if (!wasConnected) {
            wasConnected = true;
        }
    }

    /**
     * what happens when the server has been disconnected
     */
    @Override
    synchronized public void notifyOnServerDisconnected() {
        wasConnected = false;
    }

    /**
     * what happens once the service has been created or disconnected
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            YieldsApplication.setBinder((YieldServiceBinder) service);
            YieldsApplication.getBinder().attachActivity(LoggingInActivity.this);
            YieldsApplication.getBinder().connectionStatus();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}
