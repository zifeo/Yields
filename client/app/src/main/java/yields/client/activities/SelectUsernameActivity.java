package yields.client.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import yields.client.R;

public class SelectUsernameActivity extends AppCompatActivity {
    private static Toast mToast = null;

    private EditText mEditTextCreateAccount;

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
            displayError(getString(R.string.messageUsernameContainsSpaces));
        }
        else if (username.length() < 1){
            displayError(getString(R.string.messageUsernameTooShort));
        }
        else {
            Intent intent = new Intent(this, CreatingAccountActivity.class);

            // We start the CreatingAccountActivity and send it the username
            intent.putExtra(CreatingAccountActivity.USERNAME, username);
            startActivity(intent);
        }
    }

    private void displayError(String error){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        if (mToast != null){
            mToast.cancel();
        }

        mToast = Toast.makeText(context, error, duration);
        mToast.show();
    }
}
