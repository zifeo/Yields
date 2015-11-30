package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import yields.client.R;
import yields.client.exceptions.MissingIntentExtraException;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterUsersGroupsCheckBox;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.servicerequest.GroupCreateRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Main activity for group creation, displayed after the group's name selection
 * In this activity, the user is able to go to AddUsersFromEntourageActivity to add contacts
 */
public class CreateGroupActivity extends AppCompatActivity {
    private ListAdapterUsersGroupsCheckBox mAdapterUsersGroupsCheckBox;
    private List<User> mUsers;
    private List<Group> mGroups;
    private ListView mListView;

    private String mGroupName;
    private Group.GroupVisibility mGroupType;

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
        mGroupType = Group.GroupVisibility.valueOf(
                intent.getStringExtra(CreateGroupSelectNameActivity.GROUP_TYPE_KEY));
        Log.d("CreateGroupActivity", "Group type = " +
                intent.getStringExtra(CreateGroupSelectNameActivity.GROUP_TYPE_KEY));

        mUsers = new ArrayList<>();
        mUsers.add(YieldsApplication.getUser());

        mGroups = new ArrayList<>();

        mAdapterUsersGroupsCheckBox = new ListAdapterUsersGroupsCheckBox(getApplicationContext(),
                R.layout.add_user_layout, mUsers, mGroups);

        mListView = (ListView) findViewById(R.id.listViewCreateGroup);

        mListView.setAdapter(mAdapterUsersGroupsCheckBox);

        /* This indicates the app that we don't want to add the group
        currently in YieldsApplication.getGroupAdded() */
        YieldsApplication.setGroupAddedValid(false);
    }

    /**
     * Method called when this activity is resumed
     */
    @Override
    public void onResume() {
        super.onResume();

        if (YieldsApplication.isGroupAddedValid()){
            Group group = YieldsApplication.getGroupAdded();

            if (!mGroups.contains(group)) {
                mGroups.add(group);
            }

            YieldsApplication.setGroupAddedValid(false);

            mAdapterUsersGroupsCheckBox.notifyDataSetChanged();
        }
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
                    emailList.add(mUsers.get(i).getEmail());
                }

                Intent intentSelectUsers = new Intent(this, AddUsersFromEntourageActivity.class);
                intentSelectUsers.putStringArrayListExtra(AddUsersFromEntourageActivity.
                        EMAIL_LIST_INPUT_KEY, emailList);

                startActivityForResult(intentSelectUsers, REQUEST_ADD_CONTACT);
            break;

            case R.id.actionAddNodeToGroup:
                Intent intent = new Intent(this, SearchGroupActivity.class);
                intent.putExtra(SearchGroupActivity.MODE_KEY,
                        SearchGroupActivity.Mode.ADD_NODE_NEW_GROUP.ordinal());

                startActivity(intent);
                break;

            case R.id.actionDoneCreateGroup:
                List<User> userList = new ArrayList<>();

                for (int i = 0; i < mUsers.size(); i++){
                    userList.add(mUsers.get(i));
                }

                Group group = new Group(mGroupName, new Id(1), userList);
                group.setVisibility(mGroupType);
                YieldsApplication.getUser().addGroup(group);
                YieldsApplication.getBinder().sendRequest(
                        new GroupCreateRequest(YieldsApplication.getUser(), group));

                Intent createGroupIntent = new Intent(this, GroupActivity.class);
                createGroupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

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
                    if (mUsers.get(j).getEmail().equals(emailList.get(i))){
                        found = true;
                    }
                }

                if (!found){
                    for (int j = 0; j < entourage.size(); j++){ // find the user from its email
                        if (entourage.get(j).getEmail().equals(emailList.get(i))){
                            mUsers.add(entourage.get(j));
                        }
                    }
                }
            }

            mAdapterUsersGroupsCheckBox.notifyDataSetChanged();
        }
    }
}
