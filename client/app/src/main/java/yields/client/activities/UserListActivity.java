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
public class UserListActivity extends NotifiableActivity {

    private ListAdapterUsers mArrayAdapter;

    public final static String TITLE_KEY = "TITLE";
    public final static String SHOW_ADD_ENTOURAGE_KEY = "SHOW_ADD_ENTOURAGE";

    /**
     * Method automatically called on the creation of the activity
     *
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

        if (!intent.hasExtra(SHOW_ADD_ENTOURAGE_KEY)) {
            throw new MissingIntentExtraException(
                    "Show add entourage extra is missing from intent in UserListActivity");
        }

        final List<User> userList = YieldsApplication.getUserList();

        Objects.requireNonNull(YieldsApplication.getUserList(),
                "The user list in YieldsApplication cannot be null when UserListActivity is created");

        ListView listView = (ListView) findViewById(R.id.listViewUsers);

        mArrayAdapter = new ListAdapterUsers(getApplicationContext(),
                R.layout.user_layout, userList, intent.getBooleanExtra(SHOW_ADD_ENTOURAGE_KEY, false));

        listView.setAdapter(mArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < userList.size()) {
                    YieldsApplication.setUserSearched(userList.get(position));

                    Intent intent = new Intent(UserListActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(UserListActivity.this, AddUserToEntourageActivity.class);
                    startActivity(intent);
                }
            }
        });
        listView.setItemsCanFocus(true);
    }

    /**
     * Automatically called when the activity is resumed after another
     * activity was displayed.
     */
    @Override
    public void onResume(){
        super.onResume();
        mArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyChange(Change changed) {
        switch (changed) {
            case ENTOURAGE_UPDATE:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mArrayAdapter.clear();
                        mArrayAdapter.addAll(YieldsApplication.getUser().getEntourage());
                        mArrayAdapter.notifyDataSetChanged();
                    }
                });
        }
    }

    @Override
    public void notifyOnServerConnected() {

    }

    @Override
    public void notifyOnServerDisconnected() {

    }

    /**
     * Method used to take care of clicks on the tool bar
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
