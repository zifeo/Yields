package yields.client.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import yields.client.R;
import yields.client.yieldsapplication.YieldsApplication;

public class SelectUsernameActivity extends AppCompatActivity {
    private static Toast mToast = null;

    private EditText mEditTextCreateAccount;

    /**
     * OnCrate method for the SelectUsernameActivity.
     * @param savedInstanceState The bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_username);

        mEditTextCreateAccount = (EditText) findViewById(R.id.editTextCreateAccount);
    }

    /** Called when the user clicks the "Create Account" button */
    public void createAccount(View view) {
        String username = mEditTextCreateAccount.getText().toString();

        if (username.contains(" ")){
            String message = getString(R.string.messageUsernameContainsSpaces);
            YieldsApplication.showToast(getApplicationContext(), message);
        }
        else if (username.length() < 1){
            String message = getString(R.string.messageUsernameTooShort);
            YieldsApplication.showToast(getApplicationContext(), message);
        }
        else {
            Intent intent = new Intent(this, CreatingAccountActivity.class);

            // We start the CreatingAccountActivity and send it the username
            intent.putExtra(CreatingAccountActivity.USERNAME, username);
            startActivity(intent);
        }
    }
}
