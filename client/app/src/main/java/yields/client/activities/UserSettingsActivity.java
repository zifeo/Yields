package yields.client.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.listadapter.ListAdapterGroupSettings;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.servicerequest.GroupUpdateNameRequest;
import yields.client.servicerequest.GroupUpdateVisibilityRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserUpdateRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the user can change some settings, like its username, its image, ...
 */
public class UserSettingsActivity extends AppCompatActivity {
    public enum Settings {NAME, IMAGE}

    private ClientUser mUser;

    private static final int REQUEST_IMAGE = 1;

    private static final String TAG = "UserSettingsActivity";

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Settings");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        List<String> itemList = new ArrayList<>(Settings.values().length);

        itemList.add(Settings.NAME.ordinal(), getResources().getString(R.string.changeUserName));
        itemList.add(Settings.IMAGE.ordinal(), getResources().getString(R.string.changeUserImage));

        ListView listView = (ListView) findViewById(R.id.listViewSettings);

        ListAdapterGroupSettings arrayAdapter = new ListAdapterGroupSettings(getApplicationContext(),
                R.layout.group_settings_layout, itemList);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new CustomListener());
        listView.setItemsCanFocus(true);

        mUser = Objects.requireNonNull(YieldsApplication.getUser(),
                "getUser() in YieldsApplication cannot be null when UserSettingsActivity is created");
    }

    /** Method used to take care of clicks on the tool bar
     *
     * @param item The tool bar item clicked
     * @return true iff the click is not propagated
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Class used to take care of clicks in the listview
     */
    private class CustomListener implements AdapterView.OnItemClickListener {

        /**
         * Method called when an item in the listview is clicked
         *
         * @param parent   The AdapterView
         * @param view     The view clicked
         * @param position The position in the list
         * @param id       The id of the view
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Settings[] settings = Settings.values();
            switch (settings[position]) {
                case NAME:
                    changeNameListener();
                    break;

                default:
                    changeImageListener();
                    break;
            }
        }

        /**
         * Listener for the "Change username" item.
         */
        private void changeNameListener() {
            final EditText editTextName = new EditText(UserSettingsActivity.this);
            editTextName.setId(R.id.editText);
            editTextName.setText(mUser.getName());

            new AlertDialog.Builder(UserSettingsActivity.this)
                    .setTitle("Change username")
                    .setMessage("Type your new name !")
                    .setView(editTextName)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String newName = editTextName.getText().toString();
                            String message = "Username changed to \"" + newName + "\"";
                            YieldsApplication.showToast(getApplicationContext(), message);

                            ServiceRequest request = new UserUpdateRequest(mUser);
                            YieldsApplication.getBinder().sendRequest(request);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }

        /**
         * Listener for the "Change picture" item.
         */
        private void changeImageListener() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE);
        }
    }
}
