package yields.client.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import yields.client.R;
import yields.client.servicerequest.UserEntourageAddRequest;
import yields.client.servicerequest.UserSearchRequest;
import yields.client.yieldsapplication.YieldsApplication;

public class AddUserToEntourageActivity extends NotifiableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_to_entourage);

        Button b = (Button) findViewById(R.id.addUserToEntourageButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser(v);
            }
        });
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
            case NOT_EXIST:
                Log.d("Y:" + this.getClass().getName(), "not existing user");
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

        YieldsApplication.getBinder().sendRequest(
                new UserSearchRequest(YieldsApplication.getUser().getId(), email));

    }
}
