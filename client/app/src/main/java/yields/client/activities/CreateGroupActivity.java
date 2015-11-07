package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import yields.client.R;
import yields.client.exceptions.MissingIntentExtraException;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterUsersCheckBox;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Main activity for group creation, displayed after the group's name selection
 * In this activity, the user is able to go to AddUsersFromEntourageActivity to add contacts
 */
public class CreateGroupActivity extends AppCompatActivity {
    private ListAdapterUsersCheckBox mAdapterUsersCheckBox;
    private List<Map.Entry<User, Boolean> > mUsers;
    private ListView mListView;

    private String mGroupName;
    private int mGroupType; // maybe useful later

    private static final String TAG = "CreateGroupActivity";
    private static final int REQUEST_ADD_CONTACT = 1;

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        Intent intent = getIntent();

        if (!intent.hasExtra(CreateGroupSelectNameActivity.GROUP_NAME_KEY)){
            throw new MissingIntentExtraException(
                    "Group name extra is missing from intent in CreateGroupActivity");
        }

        if (!intent.hasExtra(CreateGroupSelectNameActivity.GROUP_TYPE_KEY)){
            throw new MissingIntentExtraException(
                    "Group type extra is missing from intent in CreateGroupActivity");
        }

        mGroupName = intent.getStringExtra(CreateGroupSelectNameActivity.GROUP_NAME_KEY);
        mGroupType = intent.getIntExtra(CreateGroupSelectNameActivity.GROUP_TYPE_KEY,
                CreateGroupSelectNameActivity.PUBLIC_GROUP);

        mUsers = new ArrayList<>();
        mUsers.add(new AbstractMap.SimpleEntry<User, Boolean>(YieldsApplication.getUser(), true));

        mAdapterUsersCheckBox = new ListAdapterUsersCheckBox(getApplicationContext(),
                R.layout.add_user_layout, mUsers, true);

        mListView = (ListView) findViewById(R.id.listViewCreateGroup);

        mListView.setAdapter(mAdapterUsersCheckBox);

        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkboxUser);

                boolean b = mUsers.get(position).getValue();

                checkBox.setChecked(!b);
                mUsers.get(position).setValue(!b);
            }
        });

        mListView.setItemsCanFocus(false);

        mAdapterUsersCheckBox.notifyDataSetChanged();
    }

    /**
     * Method automatically called for the tool bar items
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_create_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Method used to take care of clicks on the tool bar
     *
     * @param item The tool bar item clicked
     * @return true iff the click is not propagated
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionCancelCreateGroup:
                finish();
            break;

            case R.id.actionAddContactToGroup:
                ArrayList<String> emailList = new ArrayList<>();

                for (int i = 0; i < mUsers.size(); i++){
                    emailList.add(mUsers.get(i).getKey().getEmail());
                }

                Intent intentSelectUsers = new Intent(this, AddUsersFromEntourageActivity.class);
                intentSelectUsers.putStringArrayListExtra(AddUsersFromEntourageActivity.
                        EMAIL_LIST_INPUT_KEY, emailList);

                startActivityForResult(intentSelectUsers, REQUEST_ADD_CONTACT);
            break;

            case R.id.actionAddNodeToGroup:

            break;

            case R.id.actionDoneCreateGroup:
                List<User> userList = new ArrayList<>();

                for (int i = 0; i < mUsers.size(); i++){
                    userList.add(mUsers.get(i).getKey());
                }

                Group group = new Group(mGroupName, new Id(1), userList);

                try {
                    YieldsApplication.getUser().createNewGroup(group);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent createGroupIntent = new Intent(this, GroupActivity.class);
                createGroupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(createGroupIntent);
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    /**
     * Method called when the user returns to it after adding contacts
     * @param requestCode the code for the request
     * @param resultCode code indicating if the operation was successful or not
     * @param data the data sent by the other activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_CONTACT && resultCode == RESULT_OK) {

            ArrayList<String> emailList = data.getStringArrayListExtra(
                    AddUsersFromEntourageActivity.EMAIL_LIST_KEY);

            List<User> entourage = YieldsApplication.getUser().getEntourage();
            for (int i = 0; i < emailList.size(); i++){

                boolean found = false;
                for (int j = 0; j < mUsers.size(); j++){ // check if user is already present
                    if (mUsers.get(j).getKey().getEmail().equals(emailList.get(i))){
                        found = true;
                    }
                }

                if (!found){
                    for (int j = 0; j < entourage.size(); j++){ // find the user from its email
                        if (entourage.get(j).getEmail().equals(emailList.get(i))){
                            mUsers.add(new AbstractMap.SimpleEntry<User, Boolean>(entourage.get(j), true));
                        }
                    }
                }
            }

            mAdapterUsersCheckBox.notifyDataSetChanged();
        }
    }

}
