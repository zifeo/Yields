package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import yields.client.R;
import yields.client.exceptions.IllegalIntentExtraException;
import yields.client.exceptions.MissingIntentExtraException;
import yields.client.listadapter.ListAdapterSearchedGroups;
import yields.client.node.Group;
import yields.client.servicerequest.NodeSearchRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the user can search for new groups, based
 * on their name or tags.
 */
public class SearchGroupActivity extends NotifiableActivity{
    public enum Mode {SEARCH, ADD_NODE_NEW_GROUP, ADD_NODE_EXISTING_GROUP};

    public final static String MODE_KEY = "MODE";

    private EditText mEditTextSearch;
    private ActionBar mActionBar;
    private List<Group> mCurrentGroups;
    private ListAdapterSearchedGroups mAdapterCurrentGroups;

    private TextView mTextViewInfo;
    private ListView mListView;
    private ProgressBar mProgressBar;

    private Mode mMode;

     /*Used to not launch requests each time the user types a new character
     but rather waits a second before doing it */
    private Timer mTimer = null;
    private int mRequestsCount;

    private Timer mTemporaryTimer;

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setTitle(null);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (!intent.hasExtra(MODE_KEY)) {
            throw new MissingIntentExtraException(
                    "Mode extra is missing from intent in SearchGroupActivity");
        }

        int indexMode = intent.getIntExtra(MODE_KEY, 0);

        if (indexMode < 0 || indexMode >= Mode.values().length){
            throw new IllegalIntentExtraException(
                    "Mode extra must be between 0 and "
                            + (Mode.values().length - 1) +  " in SearchGroupActivity");
        }

        mMode = Mode.values()[indexMode];

        mTextViewInfo = (TextView) findViewById(R.id.textViewInfoSearch);

        mCurrentGroups = new ArrayList<>();
        mAdapterCurrentGroups = new ListAdapterSearchedGroups(getApplicationContext(),
                R.layout.group_searched_layout, mCurrentGroups);

        mListView = (ListView) findViewById(R.id.listViewGroupsSearched);
        mListView.setAdapter(mAdapterCurrentGroups);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YieldsApplication.setGroup(mCurrentGroups.get(position));

                Intent intent = new Intent(SearchGroupActivity.this, GroupInfoActivity.class);
                intent.putExtra(SearchGroupActivity.MODE_KEY, mMode.ordinal());
                startActivity(intent);
            }
        });

        mTextViewInfo = (TextView) findViewById(R.id.textViewInfoSearch);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarSearch);

        setStartingState();
    }

    /**
     * Method automatically called when the tool bar is created
     * @param menu The menu of the activity
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        openSearch();

        return super.onPrepareOptionsMenu(menu);
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

    /**
     * Called when the user clicks on the 'Search' button
     */
    private void openSearch(){
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setCustomView(R.layout.search_bar_layout);

        mEditTextSearch = (EditText)mActionBar.getCustomView().findViewById(R.id.editTextSearch);
        mEditTextSearch.requestFocus();

        mEditTextSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (mTimer != null) {
                    mTimer.cancel();
                }

                if (mEditTextSearch.length() == 0) {
                    setStartingState();
                } else {
                    setWaitingState();

                    mTimer = new Timer("DelayedRequestTimer");
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            launchSearch(mEditTextSearch.getText().toString());
                        }
                    }, 1000);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        mEditTextSearch.requestFocus();
    }

    /**
     * Method called when the text in the tool bar is modified
     * @param text The new text to search for
     */
    private void launchSearch(final String text){
        // Need to run on the UI thread because some views are modified
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestsCount++;

                ServiceRequest searchRequest = new NodeSearchRequest(YieldsApplication
                        .getUser().getId(), text);

                YieldsApplication.getBinder().sendRequest(searchRequest);
            }
        });
    }

    /**
     * This methods changes the look of the activity to 'starting',
     * with a message indicating that the user can search for groups
     */
    private void setStartingState(){
        mTextViewInfo.setVisibility(View.VISIBLE);
        mTextViewInfo.setText(getText(R.string.startSearchInfo));

        mProgressBar.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
    }

    /**
     * This methods changes the look of the activity to 'waiting'
     */
    private void setWaitingState(){
        mTextViewInfo.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
    }

    /**
     * This methods sets the appropriate views to visible or invisible
     * when new result is received
     */
    private void setNewResultsState(){
        mTextViewInfo.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
    }

    /**
     * This methods sets the appropriate views to visible or invisible when
     * no groups match the query
     */
    private void setNoResultsState(){
        mTextViewInfo.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);

        mTextViewInfo.setText(getText(R.string.noGroupFound));
    }

    /**
     * Notify the activity that the
     * data set has changed
     */
    @Override
    public void notifyChange(Change change) {
        switch (change) {
            case GROUP_SEARCH:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRequestsCount--;

                        mCurrentGroups.clear();
                        mCurrentGroups.addAll(YieldsApplication.getGroupsSearched());

                        if (mRequestsCount == 0) {
                            if (mCurrentGroups.size() == 0) {
                                setNoResultsState();
                            } else {
                                setNewResultsState();
                            }
                            mAdapterCurrentGroups.notifyDataSetChanged();
                        } else {
                            setWaitingState();
                        }
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

    }

    /**
     * Method called when the server is disconnected
     */
    @Override
    public void notifyOnServerDisconnected() {

    }
}
