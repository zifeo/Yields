package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
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
}
