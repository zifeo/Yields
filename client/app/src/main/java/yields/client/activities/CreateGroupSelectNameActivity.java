package yields.client.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import yields.client.R;

public class CreateGroupSelectNameActivity extends AppCompatActivity {
    public final static String GROUP_NAME_KEY = "name";
    public final static String GROUP_TYPE_KEY = "type";

    public final static int PUBLIC_GROUP = 1; // maybe create an enum in Group.java
    public final static int PRIVATE_GROUP = 2;

    private static Toast mToast = null;

    private EditText mEditText;
    private RadioButton mRadioPublic; // maybe useful later
    private RadioButton mRadioPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_select_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mEditText = (EditText) findViewById(R.id.editTextSelectGroupName);

        mRadioPublic = (RadioButton) findViewById(R.id.radioButtonPublicGroup);
        mRadioPrivate = (RadioButton) findViewById(R.id.radioButtonPrivateGroup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_create_group_select_name, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String groupName = mEditText.getText().toString();

        if (groupName.length() < 1){
            displayError(getString(R.string.messageGroupNameTooShort));
        }
        else {
            int groupType = PUBLIC_GROUP;

            if (mRadioPrivate.isChecked()){
                groupType = PRIVATE_GROUP;
            }

            Intent intentSelectName = new Intent(this, CreateGroupActivity.class);
            intentSelectName.putExtra(GROUP_NAME_KEY, groupName);
            intentSelectName.putExtra(GROUP_TYPE_KEY, groupType);

            startActivity(intentSelectName);
        }

        return true;
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
