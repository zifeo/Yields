package yields.client.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.listadapter.ListAdapterSettings;
import yields.client.listadapter.ListAdapterUsers;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Class used to display a list of users, set in YieldsApplication
 */
public class UserListActivity extends AppCompatActivity {
    private List<User> mUsers;

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Group group = Objects.requireNonNull(YieldsApplication.getGroup(),
                "The group in YieldsApplication cannot be null when UserListActivity is created");
        List<User> userList = Objects.requireNonNull(YieldsApplication.getUserList(),
                "The user list in YieldsApplication cannot be null when UserListActivity is created");

        String title = "Users of " + group.getName();
        getSupportActionBar().setTitle(title);

        ListView listView = (ListView) findViewById(R.id.listViewUsers);

        ListAdapterUsers arrayAdapter = new ListAdapterUsers(getApplicationContext(),
                R.layout.user_layout, userList);

        listView.setAdapter(arrayAdapter);
        //listView.setOnItemClickListener(new CustomListener());
        listView.setItemsCanFocus(true);
    }
}
