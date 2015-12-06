package yields.client.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import yields.client.R;
import yields.client.yieldsapplication.YieldsApplication;

public class SelectUsernameActivity extends AppCompatActivity {
    private static Toast mToast = null;

    private EditText mEditTextCreateAccount;

    /**
     * OnCreate method for the SelectUsernameActivity.
     * @param savedInstanceState The bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_username);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mEditTextCreateAccount = (EditText) findViewById(R.id.editTextCreateAccount);
    }

    /**
     * Method automatically called for the tool bar items
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_select_username, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Method called when the user clicks on 'Done'
     * @param item The tool bar item clicked
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String username = mEditTextCreateAccount.getText().toString();

        if (username.contains(" ")){
            String message = getString(R.string.messageUsernameContainsSpaces);
            YieldsApplication.showToast(getApplicationContext(), message);
        }
        else if (username.length() < getResources().getInteger(R.integer.minimumNameSize)){
            String message = getString(R.string.messageUsernameTooShort);
            YieldsApplication.showToast(getApplicationContext(), message);
        }
        else {
            Intent intent = new Intent(this, CreatingAccountActivity.class);

            // We start the CreatingAccountActivity and send it the username
            intent.putExtra(CreatingAccountActivity.USERNAME, username);
            startActivity(intent);
        }

        return true;
    }
}
