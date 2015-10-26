package yields.client.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yields.client.R;
import yields.client.gui.PairUserBoolean;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterUsersCheckBox;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class CreateGroupActivity extends AppCompatActivity {
    private ListAdapterUsersCheckBox mAdapterUsersCheckBox;
    private List<PairUserBoolean> mUsers;
    private ListView mListView;

    private String mGroupName;
    private int mGroupType;

    private static final String TAG = "CreateGroupActivity";
    private static final int REQUEST_ADD_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mGroupName = getIntent().getStringExtra(CreateGroupSelectNameActivity.GROUP_NAME_KEY);
        mGroupType = getIntent().getIntExtra(CreateGroupSelectNameActivity.GROUP_TYPE_KEY,
                CreateGroupSelectNameActivity.PUBLIC_GROUP);

        mUsers = new ArrayList<>();
        mUsers.add(new PairUserBoolean(YieldsApplication.getUser(), true));

        mAdapterUsersCheckBox = new ListAdapterUsersCheckBox(getApplicationContext(),
                R.layout.add_user_layout, mUsers, true);

        mListView = (ListView) findViewById(R.id.listViewCreateGroup);

        mListView.setAdapter(mAdapterUsersCheckBox);

        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkboxUser);

                boolean b = mUsers.get(position).getBoolean();

                checkBox.setChecked(!b);
                mUsers.get(position).setBoolean(!b);
            }
        });

        mListView.setItemsCanFocus(false);

        mAdapterUsersCheckBox.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_create_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Method used to take care of clicks on the tool bar
     *
     * @param item The tool bar item clicked
     * @return
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
                    emailList.add(mUsers.get(i).getUser().getEmail());
                }

                Intent intentSelectUsers = new Intent(this, CreateGroupSelectUsersActivity.class);
                intentSelectUsers.putStringArrayListExtra(CreateGroupSelectUsersActivity.
                        EMAIL_LIST_INPUT_KEY, emailList);

                startActivityForResult(intentSelectUsers, REQUEST_ADD_CONTACT);
            break;

            case R.id.actionAddNodeToGroup:

            break;

            case R.id.actionDoneCreateGroup:
                List<User> userList = new ArrayList<>();

                for (int i = 0; i < mUsers.size(); i++){
                    userList.add(mUsers.get(i).getUser());
                }

                Group group = new Group(mGroupName, new Id(1), userList);

                try {
                    YieldsApplication.getUser().addNewGroup(group);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_CONTACT && resultCode == RESULT_OK) {

            ArrayList<String> emailList = data.getStringArrayListExtra(
                    CreateGroupSelectUsersActivity.EMAIL_LIST_KEY);

            List<User> entourage = YieldsApplication.getUser().getEntourage();
            for (int i = 0; i < emailList.size(); i++){

                boolean found = false;
                for (int j = 0; j < mUsers.size(); j++){ // check if user is already present
                    if (mUsers.get(j).getUser().getEmail().equals(emailList.get(i))){
                        found = true;
                    }
                }

                if (!found){
                    for (int j = 0; j < entourage.size(); j++){ // find the user from its email
                        if (entourage.get(j).getEmail().equals(emailList.get(i))){
                            mUsers.add(new PairUserBoolean(entourage.get(j), true));
                        }
                    }
                }
            }

            mAdapterUsersCheckBox.notifyDataSetChanged();
        }
    }

}
