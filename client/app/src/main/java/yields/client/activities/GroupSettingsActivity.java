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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.ProcessingInstruction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.listadapter.ListAdapterSettings;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.servicerequest.GroupAddRequest;
import yields.client.servicerequest.GroupUpdateImageRequest;
import yields.client.servicerequest.GroupUpdateNameRequest;
import yields.client.servicerequest.GroupUpdateVisibilityRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the user can change some parameters for the group, leave it and
 * where the admin can change its name, image, users...
 */
public class GroupSettingsActivity extends AppCompatActivity {
    public enum Settings {NAME, TYPE, IMAGE, USERS, ADD_TAG}

    private Group mGroup;
    private ClientUser mUser;

    private static final int REQUEST_IMAGE = 1;
    private static final int REQUEST_ADD_USERS = 2;

    private static final String TAG = "GroupSettingsActivity";

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Settings");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        List<String> itemList = new ArrayList<>(Settings.values().length);

        itemList.add(Settings.NAME.ordinal(), getResources().getString(R.string.changeGroupName));
        itemList.add(Settings.TYPE.ordinal(), getResources().getString(R.string.changeGroupType));
        itemList.add(Settings.IMAGE.ordinal(), getResources().getString(R.string.changeGroupImage));
        itemList.add(Settings.USERS.ordinal(), getResources().getString(R.string.addUsers));
        itemList.add(Settings.ADD_TAG.ordinal(), getResources().getString(R.string.addTag));

        ListView listView = (ListView) findViewById(R.id.listViewSettings);

        ListAdapterSettings arrayAdapter = new ListAdapterSettings(getApplicationContext(),
                R.layout.group_settings_layout, itemList);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new CustomListener());
        listView.setItemsCanFocus(true);

        mGroup = YieldsApplication.getGroup();
        mUser = YieldsApplication.getUser();

        assert mGroup != null : "The group in YieldsApplication cannot be null when this activity is created" ;
        assert mUser != null : "The user in YieldsApplication cannot be null when this activity is created" ;
    }

    /**
     * Method automatically called when the user has selected the new group image
     * @param requestCode The code of the request
     * @param resultCode The code of the result
     * @param data The data where the uri, or the list of email is
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

                    String message = "Group image changed";
                    YieldsApplication.showToast(getApplicationContext(), message);

                    ServiceRequest request = new GroupUpdateImageRequest(mUser, mGroup, image);
                    YieldsApplication.getBinder().sendRequest(request);
                }
                else {
                    String message = "Could not retrieve image";
                    YieldsApplication.showToast(getApplicationContext(), message);
                }
            } catch (IOException e) {
                String message = "Could not retrieve image";
                YieldsApplication.showToast(getApplicationContext(), message);
                Log.d(TAG, message);
            }
        }
        else if (requestCode == REQUEST_ADD_USERS && resultCode == RESULT_OK){
            ArrayList<String> emailList = data.getStringArrayListExtra(
                    AddUsersFromEntourageActivity.EMAIL_LIST_KEY);

            List<User> newUsers = new ArrayList<>();
            List<User> entourage = mUser.getEntourage();
            for (int i = 0; i < emailList.size(); i++){
                for (int j = 0; j < entourage.size(); j++){
                    if (entourage.get(j).getEmail().equals(emailList.get(i))
                            && !mGroup.containsUser(entourage.get(j))){

                        newUsers.add(entourage.get(j));
                    }
                }
            }

            String message = newUsers.size() + " user(s) added to group";
            YieldsApplication.showToast(getApplicationContext(), message);

            // TODO Send request to add multiple users to server
        }
    }

    /**
     * Class used to take care of clicks in the listview
     */
    private class CustomListener implements AdapterView.OnItemClickListener {

        /**
         *  Method called when an item in the listview is clicked
         * @param parent The AdapterView
         * @param view The view clicked
         * @param position The position in the list
         * @param id The id of the view
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Settings[] settings = Settings.values();
            switch(settings[position]){
                case NAME:
                    changeNameListener();
                    break;

                case TYPE:
                    changeTypeListener();
                    break;

                case IMAGE:
                    changeImageListener();
                    break;

                case ADD_TAG:
                    changeTagListener();
                    break;

                default:
                    addUsersListener();
                    break;
            }
        }

        /**
         * Listener for the "Change group name" item.
         */
        private void changeNameListener(){
            final EditText editTextName = new EditText(GroupSettingsActivity.this);
            editTextName.setId(R.id.editText);
            editTextName.setText(mGroup.getName());

            new AlertDialog.Builder(GroupSettingsActivity.this)
                    .setTitle("Change group name")
                    .setMessage("Type the new group's name !")
                    .setView(editTextName)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String newName = editTextName.getText().toString();
                            String message = "Group name changed to \"" + newName + "\"";
                            YieldsApplication.showToast(getApplicationContext(), message);

                            ServiceRequest request = new GroupUpdateNameRequest(mUser,
                                    mGroup, newName);
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
         * Listener for the "Change group type" item.
         */
        private void changeTypeListener(){
            final CharSequence[] types = {" Public"," Private"};
            final int[] itemSelected = {0}; // used as a pointer
            AlertDialog groupTypeDialog;

            // Creating and Building the Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettingsActivity.this)
                .setTitle("Change group type")
                .setSingleChoiceItems(types, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        itemSelected[0] = item;
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String type = "public";
                        if (itemSelected[0] == 1) {
                            type = "private";
                        }

                        String message = "Group type changed to : " + type;
                        YieldsApplication.showToast(getApplicationContext(), message);

                        Group.GroupVisibility visibility;
                        if (itemSelected[0] == 1) {
                            visibility = Group.GroupVisibility.PRIVATE;
                        }
                        else {
                            visibility = Group.GroupVisibility.PUBLIC;
                        }
                        ServiceRequest request = new GroupUpdateVisibilityRequest(mUser,
                                mGroup, visibility);
                        YieldsApplication.getBinder().sendRequest(request);
                    }
                })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
            groupTypeDialog = builder.create();
            groupTypeDialog.show();
        }

        /**
         * Listener for the "Change group image" item.
         */
        private void changeImageListener(){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE);
        }

        /**
         * Listener for the "Add users" item.
         */
        private void addUsersListener(){
            ArrayList<String> emailList = new ArrayList<>();
            List<User> currentUsers = mGroup.getUsers();

            for (int i = 0; i < currentUsers.size(); i++){
                emailList.add(currentUsers.get(i).getEmail());
            }

            Intent intentSelectUsers = new Intent(GroupSettingsActivity.this, AddUsersFromEntourageActivity.class);
            intentSelectUsers.putStringArrayListExtra(AddUsersFromEntourageActivity.
                    EMAIL_LIST_INPUT_KEY, emailList);

            startActivityForResult(intentSelectUsers, REQUEST_ADD_USERS);
        }

        /**
         * Listener for the "Add tag" item.
         */
        private void changeTagListener() {
            final EditText editTextTag = new EditText(GroupSettingsActivity.this);
            editTextTag.setId(R.id.editText);

            final AlertDialog dialog = new AlertDialog.Builder(GroupSettingsActivity.this)
                    .setTitle("Add new tag")
                    .setMessage("The new tag must be between 2 and 20 characters" +
                            ", in lowercase, with no spaces.")
                    .setView(editTextTag)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text = editTextTag.getText().toString();

                    if (text.length() < Group.Tag.MIN_TAG_LENGTH) {
                        YieldsApplication.showToast(getApplicationContext(),
                                "The tag is too short");
                    } else if (text.length() > Group.Tag.MAX_TAG_LENGTH) {
                        YieldsApplication.showToast(getApplicationContext(),
                                "The tag is too long");
                    } else if (!text.toLowerCase().equals(text)) {
                        YieldsApplication.showToast(getApplicationContext(),
                                "The tag is not in lowercase");
                    } else if (text.contains(" ")) {
                        YieldsApplication.showToast(getApplicationContext(),
                                "The tag contains spaces");
                    } else {
                        YieldsApplication.showToast(getApplicationContext(),
                                "Tag \"" + text + "\" added");

                        Group.Tag tag = new Group.Tag(text);

                        mGroup.addTag(tag);

                        // TODO Send request to server to add tag in database

                        dialog.dismiss();
                    }
                }
            });
        }
    }

}
