package yields.client.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.plus.Plus;

import java.io.IOException;

import yields.client.R;
import yields.client.id.Id;
import yields.client.serverconnection.ConnectionManager;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerChannel;
import yields.client.serverconnection.YieldEmulatorSocketProvider;
import yields.client.service.YieldService;
import yields.client.service.YieldServiceBinder;
import yields.client.yieldsapplication.YieldsApplication;

public class LoggingInActivity extends AppCompatActivity {

    /**
     * onCreate method for the LoggingInActivity.
     * @param savedInstanceState The bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging_in);

        Intent serviceIntent = new Intent(this, YieldService.class)
                .putExtra("email", Plus.AccountApi
                        .getAccountName(YieldsApplication.getGoogleApiClient()));
        startService(serviceIntent);

        Intent serviceBindingIntent = new Intent(this, YieldService.class)
                .putExtra("bindGroupActivity", true);

        bindService(serviceBindingIntent, mConnection, Context.BIND_AUTO_CREATE);

        goToGroupActivity();
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

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            YieldsApplication.setBinder((YieldServiceBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("DEBUG", "disconnect");
        }
    };
}
