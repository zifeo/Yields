package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.exceptions.MissingIntentExtraException;
import yields.client.listadapter.ListAdapterUsers;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Class used to display a list of users, set in YieldsApplication
 */
public class UserListActivity extends AppCompatActivity {

    public final static String TITLE_KEY = "TITLE";

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

        Intent intent = getIntent();
        if (!intent.hasExtra(TITLE_KEY)) {
            throw new MissingIntentExtraException(
                    "Title extra is missing from intent in UserListActivity");
        }

        getSupportActionBar().setTitle(intent.getStringExtra(TITLE_KEY));

        final List<User> userList = Objects.requireNonNull(YieldsApplication.getUserList(),
                "The user list in YieldsApplication cannot be null when UserListActivity is created");

        ListView listView = (ListView) findViewById(R.id.listViewUsers);

        ListAdapterUsers arrayAdapter = new ListAdapterUsers(getApplicationContext(),
                R.layout.user_layout, userList);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YieldsApplication.setUserSearched(userList.get(position));

                Intent intent = new Intent(UserListActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
        listView.setItemsCanFocus(true);
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
}
