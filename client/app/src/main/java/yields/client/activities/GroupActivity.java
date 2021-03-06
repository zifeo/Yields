package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yields.client.R;
import yields.client.listadapter.ListAdapterGroups;
import yields.client.node.Group;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Central activity of Yields where the user can discover new nodes, create groups, see its contact
 * list, change its settings and go to chats of different groups
 */
public class GroupActivity extends NotifiableActivity {
    private ListAdapterGroups mAdapterGroups;
    private List<Group> mGroups;

    private TextView mTextViewNoGroup;

    /* String used for debug log */
    private static final String TAG = "GroupActivity";

    /**
     * Method automatically called on the creation of the activity
     *
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mGroups = new ArrayList<>();

        mAdapterGroups = new ListAdapterGroups(getApplicationContext(), R.layout.group_layout, mGroups);

        ListView listView = (ListView) findViewById(R.id.listViewGroups);

        listView.setAdapter(mAdapterGroups);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group chosenGroup = mGroups.get(position);
                YieldsApplication.setGroup(chosenGroup);

                Intent intent = new Intent(GroupActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });
        listView.setItemsCanFocus(false);

        View connectionStatusView = findViewById(R.id.connectionStatus);

        connectionStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YieldsApplication.getBinder().reconnect();
            }
        });

        mTextViewNoGroup = (TextView) findViewById(R.id.textViewNoGroup);
        mTextViewNoGroup.setText(getString(R.string.messageNoGroup));
        checkNoGroup();

        YieldsApplication.setResources(getResources());
        YieldsApplication.setApplicationContext(getApplicationContext());
    }

    /**
     * Method automatically called for the tool bar items
     *
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method used to take care of clicks on the tool bar
     *
     * @param item The tool bar item clicked
     * @return true iff the click is not propagated
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.actionEntourage:
                intent = new Intent(this, UserListActivity.class);
                String title = "Entourage";
                intent.putExtra(UserListActivity.TITLE_KEY, title);
                intent.putExtra(UserListActivity.SHOW_ADD_ENTOURAGE_KEY, true);

                YieldsApplication.setUserList(YieldsApplication.getUser().getEntourage());
                break;

            case R.id.actionDiscover:
                intent = new Intent(this, SearchGroupActivity.class);
                intent.putExtra(SearchGroupActivity.MODE_KEY,
                        SearchGroupActivity.Mode.SEARCH.ordinal());
                break;

            case R.id.actionCreate:
                intent = new Intent(this, CreateGroupSelectNameActivity.class);
                break;

            case R.id.actionSettings:
                intent = new Intent(this, UserSettingsActivity.class);
                break;

            default:
                return super.onOptionsItemSelected(item);

        }

        if (intent == null) {
            throw new IllegalStateException("Itent should never be null at this point");
        } else {
            startActivity(intent);
        }

        return intent != null;
    }

    /**
     * Notify the activity that the
     * data set has changed
     */
    @Override
    public void notifyChange(Change change) {
        switch (change) {
            case GROUP_LIST:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapterGroups.clear();
                        mAdapterGroups.addAll(YieldsApplication.getUser().getUserGroups());
                        mAdapterGroups.notifyDataSetChanged();

                        checkNoGroup();
                    }
                });
                break;
            default:
                Log.d("Y:" + this.getClass().getName(), "useless notify change...");
        }
    }

    /**
     * Method called when the server is connected
     */
    @Override
    public void notifyOnServerConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.connectionStatus).setVisibility(View.GONE);
            }
        });
    }

    /**
     * Method called when the server is disconnected
     */
    @Override
    public void notifyOnServerDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.connectionStatus).setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Automatically called when the activity is started
     */
    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * Automatically called when the activity is resumed
     */
    @Override
    public void onResume() {
        super.onResume();
        mAdapterGroups.clear();
        mAdapterGroups.addAll(YieldsApplication.getUser().getUserGroups());
        mAdapterGroups.notifyDataSetChanged();

        checkNoGroup();
    }

    /**
     * Method that sets the right visibility to the
     * textView depending on the number of groups
     */
    private void checkNoGroup() {
        if (mGroups.isEmpty()) {
            mTextViewNoGroup.setVisibility(View.VISIBLE);
        } else {
            mTextViewNoGroup.setVisibility(View.GONE);
        }
    }
}
