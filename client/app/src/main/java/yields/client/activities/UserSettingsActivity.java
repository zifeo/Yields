package yields.client.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.listadapter.ListAdapterUserSettings;
import yields.client.node.ClientUser;
import yields.client.serverconnection.YieldEmulatorSocketProvider;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserUpdateNameRequest;
import yields.client.servicerequest.UserUpdateRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the user can change some settings, like its username, its image, ...
 */
public class UserSettingsActivity extends AppCompatActivity {
    public enum Settings {INFO, NAME, IMAGE, REMOVE_ENTOURAGE, LOGOUT}

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
        getSupportActionBar().setTitle("User Settings");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        List<String> itemList = new ArrayList<>(Settings.values().length);

        itemList.add(Settings.INFO.ordinal(), getResources().getString(R.string.userInfo));
        itemList.add(Settings.NAME.ordinal(), getResources().getString(R.string.changeUserName));
        itemList.add(Settings.IMAGE.ordinal(), getResources().getString(R.string.changeUserImage));
        itemList.add(Settings.REMOVE_ENTOURAGE.ordinal(), getResources().getString(R.string.removeFromEntourage));
        itemList.add(Settings.LOGOUT.ordinal(), getResources().getString(R.string.logout));

        ListView listView = (ListView) findViewById(R.id.listViewSettings);

        ListAdapterUserSettings arrayAdapter = new ListAdapterUserSettings(getApplicationContext(),
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
     * Method automatically called when the user has selected the new group image
     *
     * @param requestCode The code of the request
     * @param resultCode  The code of the result
     * @param data        The data where the uri, or the list of email is
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            Objects.requireNonNull(data);
            Objects.requireNonNull(data.getData());

            Uri uri = data.getData();

            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if (image != null) {

                    String message = "User picture changed";
                    YieldsApplication.showToast(getApplicationContext(), message);

                    int diameter = getResources().getInteger(R.integer.largeGroupImageDiameter);
                    mUser.setImg(Bitmap.createScaledBitmap(image, diameter, diameter, false));

                    ServiceRequest request = new UserUpdateRequest(mUser);
                    YieldsApplication.getBinder().sendRequest(request);
                } else {
                    String message = "Could not retrieve image";
                    YieldsApplication.showToast(getApplicationContext(), message);
                }
            } catch (IOException e) {
                String message = "Could not retrieve image";
                YieldsApplication.showToast(getApplicationContext(), message);
                Log.d(TAG, message);
            }
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
                case INFO:
                    seeInfoListener();
                    break;

                case NAME:
                    changeNameListener();
                    break;

                case IMAGE:
                    changeImageListener();
                    break;

                case REMOVE_ENTOURAGE:
                    removeUsersListener();
                    break;

                default:
                    logoutListener();
                    break;
            }
        }

        /**
         * Listener for the "See your info" item.
         */
        private void seeInfoListener() {
            YieldsApplication.setUserSearched(mUser);

            Intent intent = new Intent(UserSettingsActivity.this, UserInfoActivity.class);
            startActivity(intent);
        }

        /**
         * Listener for the "Change username" item.
         */
        private void changeNameListener() {
            final EditText editTextUsername = new EditText(UserSettingsActivity.this);
            editTextUsername.setId(R.id.editText);
            editTextUsername.setText(mUser.getName());
            editTextUsername.setSelection(editTextUsername.length());

            final int minimumSize = getResources().getInteger(R.integer.minimumNameSize);

            DialogInterface.OnClickListener emptyListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            };

            final AlertDialog dialog = new AlertDialog.Builder(UserSettingsActivity.this)
                    .setTitle("Change your username")
                    .setMessage("Your new username must be at least " + minimumSize + " characters long.")
                    .setView(editTextUsername)
                    .setPositiveButton("Ok", emptyListener)
                    .setNegativeButton("Cancel", emptyListener)
                    .create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newName = editTextUsername.getText().toString();

                    if (newName.length() < minimumSize) {
                        YieldsApplication.showToast(getApplicationContext(),
                                "The username is too short");
                    } else {
                        YieldsApplication.showToast(getApplicationContext(),
                                "Username changed to \"" + newName + "\" !");

                        mUser.setName(newName);

                        ServiceRequest request = new UserUpdateNameRequest(mUser);
                        YieldsApplication.getBinder().sendRequest(request);

                        dialog.dismiss();
                    }
                }
            });
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

        /**
         * Listener for the "Remove users" item.
         */
        private void removeUsersListener() {
            Intent intent = new Intent(UserSettingsActivity.this, RemoveUsersFromEntourageActivity.class);

            startActivity(intent);
        }

        /**
         * Listener for the "Logout" item.
         */
        private void logoutListener() {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
