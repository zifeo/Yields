package yields.client.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yields.client.R;
import yields.client.servicerequest.UserSearchRequest;
import yields.client.yieldsapplication.YieldsApplication;

public class AddUserToEntourageActivity extends NotifiableActivity {
    private EditText mEditTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_to_entourage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
    }

    /**
     * Method automatically called for the tool bar items
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_add_user_to_entourage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Method called when the user clicks on 'Done'
     * @param item The tool bar item clicked
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String email = mEditTextEmail.getText().toString();
        Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            String message = getString(R.string.messageWrongEmail);
            YieldsApplication.showToast(getApplicationContext(), message);
        }
        else {
            YieldsApplication.getBinder().sendRequest(
                    new UserSearchRequest(YieldsApplication.getUser().getId(), email));
        }

        return true;
    }

    /**
     * Notify the activity that the user was added to the Entourage.
     */
    @Override
    public void notifyChange(NotifiableActivity.Change change) {
        switch (change) {
            case ADD_ENTOURAGE:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String messageOk = getString(R.string.messageContactAdded);
                        YieldsApplication.showToast(getApplicationContext(), messageOk);

                        finish();
                    }
                });
                break;
            case NOT_EXIST:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String messageNoUser = getString(R.string.messageNoUser);
                        YieldsApplication.showToast(getApplicationContext(), messageNoUser);
                    }
                });
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
}
