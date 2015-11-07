package yields.client.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import yields.client.R;
import yields.client.exceptions.NodeException;
import yields.client.gui.GraphicTransforms;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterSettings;
import yields.client.messages.Message;
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

        // TO BE REMOVED
        createFakeUsersAndGroup();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Settings");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        List<String> itemList = new ArrayList<>(4);

        itemList.add(Settings.NAME.ordinal(), "Change group name");
        itemList.add(Settings.TYPE.ordinal(), "Change group type");
        itemList.add(Settings.IMAGE.ordinal(), "Change group image");
        itemList.add(Settings.USERS.ordinal(), "Add users");

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
     * @param data The data where the uri is
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
                    Toast toast = Toast.makeText(getApplicationContext(), "Group image changed !", Toast.LENGTH_SHORT);
                    toast.show();
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
     * To be removed as soon as the logging is working
     */
    private class MockClientUser extends ClientUser {

        public MockClientUser(String name, Id id, String email, Bitmap img) throws NodeException {
            super(name, id, email, img);
        }

        @Override
        public void sendMessage(Group group, Message message) {
            /* Nothing */
        }

        @Override
        public List<Message> getGroupMessages(Group group, Date lastDate) throws IOException {
            ArrayList<Message> messageList =  new ArrayList<>();
            return messageList;
        }

        @Override
        public void createNewGroup(Group group) throws IOException {

        }

        @Override
        public void deleteGroup(Group group) {
            /* Nothing */
        }

        @Override
        public Map<User, String> getHistory(Group group, Date from) {
            return null;
        }
    }

    /**
     * To be removed as soon as the logging is working
     */
    private void createFakeUsersAndGroup() {
        Bitmap imageUser = BitmapFactory.decodeResource(getResources(), R.drawable.default_user_image);
        imageUser = GraphicTransforms.getCroppedCircleBitmap(imageUser, getResources().getInteger(R.integer.groupImageDiameter));

        YieldsApplication.setGroup(new Group("SWENG", new Id(666), new ArrayList<User>()));

        try {
            YieldsApplication.setUser(new MockClientUser("Arnaud", new Id(1), "m@m.is", imageUser));
            YieldsApplication.getUser().addUserToEntourage(new MockClientUser("Nico1", new Id(2), "m@m.es", imageUser));
            YieldsApplication.getUser().addUserToEntourage(new MockClientUser("Teo", new Id(3), "m@m.fr", imageUser));
            YieldsApplication.getUser().addUserToEntourage(new MockClientUser("Justinien", new Id(4), "m@m.cn", imageUser));
            YieldsApplication.getUser().addUserToEntourage(new MockClientUser("Nico2", new Id(5), "m@m.jpp", imageUser));
            YieldsApplication.getUser().addUserToEntourage(new MockClientUser("Jeremy", new Id(6), "m@m.ch", imageUser));
        } catch (NodeException e) {
            e.printStackTrace();
        }

    }

    private class CustomListener implements AdapterView.OnItemClickListener {

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

        private void changeNameListener(){
            final EditText editTextName = new EditText(GroupSettingsActivity.this);
            editTextName.setText(mGroup.getName());

            new AlertDialog.Builder(GroupSettingsActivity.this)
                    .setTitle("Change group name")
                    .setMessage("Type the new group's name !")
                    .setView(editTextName)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String name = editTextName.getText().toString();

                            //TEMPORARY
                            Toast toast = Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }

        private void changeTypeListener(){
            final CharSequence[] types = {" Public "," Private "};
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
                        Toast toast = Toast.makeText(getApplicationContext(), "Type = " + itemSelected[0], Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
            groupTypeDialog = builder.create();
            groupTypeDialog.show();
        }

        void changeImageListener(){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE);
        }

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
