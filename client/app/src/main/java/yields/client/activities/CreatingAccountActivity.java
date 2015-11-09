package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import yields.client.R;

public class CreatingAccountActivity extends AppCompatActivity {
    public final static String USERNAME = "yields.client.activities.USERNAME";

    /**
     * onCreate method for the CreatingAccountActivity.
     * @param savedInstanceState bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_account);

        // Client user call

        // clientUser.createAccount(savedInstanceState.getString(USERNAME), this);

    }
    /**
     * Method called by clientUser when the server creates the account
     */
    public void goToGroupActivity(){
        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }

}
