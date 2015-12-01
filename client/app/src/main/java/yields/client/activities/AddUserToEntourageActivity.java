package yields.client.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import yields.client.R;
import yields.client.servicerequest.UserEntourageAddRequest;
import yields.client.yieldsapplication.YieldsApplication;

public class AddUserToEntourageActivity extends NotifiableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_to_entourage);
    }

    /**
     * Notify the activity that the user was added to the Entourage.
     */
    @Override
    public void notifyChange(NotifiableActivity.Change change) {
        switch (change) {
            case ADD_ENTOURAGE:
                    finish();
                break;
            default:
                Log.d("Y:" + this.getClass().getName(), "useless notify change...");
        }

    }

    @Override
    public void notifyOnServerConnected() {
        //Nothing as of now
    }

    @Override
    public void notifyOnServerDisconnected() {
        //Nothing as of now
    }

    /**
     * Called once the button to add the User is called.
     *
     * @param view The view of the button.
     */
    public void addUser(View view) {
        TextView v = (TextView) findViewById(R.id.emailOfUser);
        String email = v.getText().toString();

        //YieldsApplication.getBinder().sendRequest(new UserEntourageAddRequest(YieldsApplication.getUser(), ));

    }
}
