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
import yields.client.node.Group;

/**
 * The activity where the user can choose the name of the future group and it type (public / private)
 */
public class CreateGroupSelectNameActivity extends AppCompatActivity {
    public final static String GROUP_NAME_KEY = "name";
    public final static String GROUP_TYPE_KEY = "type";

    private static Toast mToast = null;

    private EditText mEditText;
    private RadioButton mRadioPublic; // maybe useful later
    private RadioButton mRadioPrivate;

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState the previous instance of the activity
     */
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

    /**
     * Method automatically called for the tool bar items
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_create_group_select_name, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Method used to take care of clicks on the tool bar
     *
     * @param item The tool bar item clicked
     * @return true iff the click is not propagated
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String groupName = mEditText.getText().toString();

        if (groupName.length() < 1){
            displayError(getString(R.string.messageGroupNameTooShort));
        }
        else {
            Group.GroupVisibility groupType = Group.GroupVisibility.PUBLIC;

            if (mRadioPrivate.isChecked()){
                groupType = Group.GroupVisibility.PRIVATE;
            }

            Intent intentSelectName = new Intent(this, CreateGroupActivity.class);
            intentSelectName.putExtra(GROUP_NAME_KEY, groupName);
            intentSelectName.putExtra(GROUP_TYPE_KEY, groupType);

            startActivity(intentSelectName);
        }

        return true;
    }

    // displays a toast with the given string
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
