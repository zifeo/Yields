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
import yields.client.yieldsapplication.YieldsApplication;

public class LoggingInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging_in);

        // Client user call

        // sEmailValid(googleApiClient.getEmail(), this);
        YieldEmulatorSocketProvider socket = null;
        try {
            socket = new YieldEmulatorSocketProvider();
            ConnectionManager connectionManager = new ConnectionManager(socket);
            ServerChannel serverChannel = (ServerChannel) connectionManager.getCommunicationChannel();
            String email = Plus.AccountApi.getAccountName(YieldsApplication.getGoogleApiClient());
            Request connectReq = RequestBuilder.userConnectRequest(new Id(0), email);
            serverChannel.sendRequest(connectReq);

            Log.d("LoggingInActivity", "Email = " + email);
            // TODO : check request, waiting for Nico...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method called by clientUser when the server indicates that the account already exists
    public void goToGroupActivity(){
        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }

    // Method called by clientUser when the server indicates that the account doesn't exist
    public void goToSelectUsernameActivity(){
        Intent intent = new Intent(this, SelectUsernameActivity.class);
        startActivity(intent);
    }
}
