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
import android.view.View;
import android.widget.AdapterView;
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
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the user can change some parameters for the group, leave it and
 * the admin can change its name, image, users...
 */
public class GroupSettingsActivity extends AppCompatActivity {
    public enum Settings {NAME, TYPE, IMAGE, USERS}

    private Group mGroup;
    private ClientUser mUser;

    private static final int REQUEST_IMAGE = 1;
    private static final int REQUEST_ADD_USERS = 2;

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

        List<String> itemList = new ArrayList<>(4);

        itemList.add(Settings.NAME.ordinal(), getResources().getString(R.string.changeGroupName));
        itemList.add(Settings.TYPE.ordinal(), getResources().getString(R.string.changeGroupType));
        itemList.add(Settings.IMAGE.ordinal(), getResources().getString(R.string.changeGroupImage));
        itemList.add(Settings.USERS.ordinal(), getResources().getString(R.string.addUsers));

        ListView listView = (ListView) findViewById(R.id.listViewSettings);

        ListAdapterSettings arrayAdapter = new ListAdapterSettings(getApplicationContext(), R.layout.group_settings_layout, itemList);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new CustomListener());
        listView.setItemsCanFocus(true);

        mGroup = YieldsApplication.getGroup();
        mUser = YieldsApplication.getUser();

        Objects.requireNonNull(mGroup,
                "The group in YieldsApplication cannot be null when this activity is created");

        Objects.requireNonNull(mUser,
                "The user in YieldsApplication cannot be null when this activity is created");
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
                    Toast toast = Toast.makeText(getApplicationContext(), "Group image changed", Toast.LENGTH_SHORT);
                    toast.show();

                    //TODO Change group image and send notification to server
                }
            } catch (IOException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Could not retrieve image", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else if (requestCode == REQUEST_ADD_USERS && resultCode == RESULT_OK){
            ArrayList<String> emailList = data.getStringArrayListExtra(
                    AddUsersFromEntourageActivity.EMAIL_LIST_KEY);

            int count = 0;

            List<User> entourage = mUser.getEntourage();
            for (int i = 0; i < emailList.size(); i++){
                for (int j = 0; j < entourage.size(); j++){
                    if (entourage.get(j).getEmail().equals(emailList.get(i))
                            && !mGroup.containsUser(entourage.get(j))){

                        // TODO Add user to group and send notification to server

                        count++;
                    }
                }
            }

            Toast toast = Toast.makeText(getApplicationContext(), count + " user(s) added to group", Toast.LENGTH_SHORT);
            toast.show();
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

            if (position == Settings.NAME.ordinal()){
                changeNameListener();
            }
            else if (position == Settings.TYPE.ordinal()){
                changeTypeListener();
            }
            else if (position == Settings.IMAGE.ordinal()){
                changeImageListener();
            }
            else {
                addUsersListener();
            }
        }

        // When the item "Change group name" is clicked
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
                            String name = "Group name changed to \"" + editTextName.getText().toString() + "\"";

                            Toast toast = Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT);
                            toast.show();

                            // TODO Add change in group's name, not just display
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }

        // When the item "Change group type" is clicked
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

                        String typeText = "Group type changed to : " + type;

                        Toast toast = Toast.makeText(getApplicationContext(), typeText, Toast.LENGTH_SHORT);
                        toast.show();

                        if (type.equals("public")) {
                            mGroup.setVisibility(Group.GroupVisibility.PUBLIC);
                        } else {
                            mGroup.setVisibility(Group.GroupVisibility.PRIVATE);
                        }
                    }
                })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
            groupTypeDialog = builder.create();
            groupTypeDialog.show();
        }

        // When the item "Change group image" is clicked
        void changeImageListener(){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE);
        }

        // When the item "Add users" is clicked
        void addUsersListener(){
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
    }

}
