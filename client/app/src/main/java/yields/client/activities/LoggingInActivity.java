package yields.client.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import yields.client.R;

public class LoggingInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging_in);

        // Client user call

        // clientUser.isEmailValid(googleApiClient.getEmail(), this);
    }

    // Method called by clientUser when the server indicates that the account already exists
    public void goToGroupActivity(){
        Log.i("Debug", "Kapoue");

        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }

    // Method called by clientUser when the server indicates that the account doesn't exist
    public void goToSelectUsernameActivity(){
        Intent intent = new Intent(this, SelectUsernameActivity.class);
        startActivity(intent);
    }
}
