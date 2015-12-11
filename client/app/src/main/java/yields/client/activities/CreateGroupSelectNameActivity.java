package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;

import yields.client.R;
import yields.client.node.Group;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * The activity where the user can choose the name of the future group and it type (public / private)
 */
public class CreateGroupSelectNameActivity extends AppCompatActivity {
    public final static String GROUP_NAME_KEY = "name";
    public final static String GROUP_TYPE_KEY = "type";

    private EditText mEditText;
    private RadioButton mRadioPrivate;
    private RadioButton mRadioPublic;
    private RadioButton mRadioRSS;

    /**
     * Method automatically called on the creation of the activity
     *
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

        mRadioPrivate = (RadioButton) findViewById(R.id.radioButtonPrivateGroup);
        mRadioPublic = (RadioButton) findViewById(R.id.radioButtonPublicGroup);
        mRadioRSS = (RadioButton) findViewById(R.id.radioButtonRss);
    }

    /**
     * Method automatically called for the tool bar items
     *
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_create_group_select_name, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method used to take care of clicks on the tool bar
     *
     * @param item The tool bar item clicked
     * @return true iff the click is not propagated
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String groupName = mEditText.getText().toString();

        if (groupName.length() < getResources().getInteger(R.integer.minimumNameSize)) {
            String message = getString(R.string.messageGroupNameTooShort);
            YieldsApplication.showToast(getApplicationContext(), message);
        } else {
            Group.GroupType groupType;
            Intent intent;

            if (mRadioPrivate.isChecked()) {
                groupType = Group.GroupType.PRIVATE;
                intent = new Intent(this, CreateGroupActivity.class);
            } else if (mRadioPublic.isChecked()) {
                groupType = Group.GroupType.PUBLISHER;
                intent = new Intent(this, CreateGroupActivity.class);
            } else {
                groupType = Group.GroupType.RSS;
                intent = new Intent(this, CreateRSSFeedActivity.class);
            }

            intent.putExtra(GROUP_NAME_KEY, groupName);
            intent.putExtra(GROUP_TYPE_KEY, groupType.toString());

            startActivity(intent);
        }

        return true;
    }
}
