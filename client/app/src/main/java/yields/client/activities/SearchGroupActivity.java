package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
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
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterSearchedGroups;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the user can search for new groups, based
 * on their name or tags.
 */
public class SearchGroupActivity extends AppCompatActivity implements NotifiableActivity{
    private MenuItem mMenuSearch;
    private MenuItem mMenuClose;
    private EditText mEditTextSearch;
    private ActionBar mActionBar;
    private List<Group> mCurrentGroups;
    private List<Group> mGlobalGroups; // to be removed when the requests are operational
    private ListAdapterSearchedGroups mAdapterCurrentGroups;

    private TextView mTextViewInfo;
    private ListView mListView;
    private ProgressBar mProgressBar;

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

        mTextViewInfo = (TextView) findViewById(R.id.textViewInfoSearch);

        createFakeGroups();
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
        mMenuSearch = menu.findItem(R.id.actionSearch);
        mMenuSearch.setVisible(false);

        mMenuClose = menu.findItem(R.id.actionCloseSearch);

        openSearch();

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Method automatically called for the tool bar items
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_group, menu);
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
            case R.id.actionSearch:
                openSearch();
                return true;

            case R.id.actionCloseSearch:
                closeSearch();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Called when the user clicks on the 'Search' button
     */
    private void openSearch(){
        mMenuSearch.setVisible(false);
        mMenuClose.setVisible(true);

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
     * Called when the user clicks on the 'Close Search' button
     */
    private void closeSearch(){
        mMenuSearch.setVisible(true);
        mMenuClose.setVisible(false);

        mActionBar.setDisplayShowCustomEnabled(false);
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

                /**
                 * When the search request is operational, the whole
                 * part below should be removed and replaced by a
                 * call to the service
                 */
                mTemporaryTimer = new Timer("FakeRequestTimer");
                mTemporaryTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        List<Group> newGroupsSearched = new ArrayList<>();

                        String tagText = text.toLowerCase().replace(' ', '_');


                        // match for the names
                        for (int i = 0; i < mGlobalGroups.size(); i++) {
                            if (mGlobalGroups.get(i).getName().toLowerCase().startsWith(tagText)) {
                                newGroupsSearched.add(mGlobalGroups.get(i));
                            }
                        }

                        if (text.length() > Group.Tag.MIN_TAG_LENGTH
                                && text.length() < Group.Tag.MAX_TAG_LENGTH) {
                            Group.Tag tag = new Group.Tag(tagText);

                            //match for the tags
                            for (int i = 0; i < mGlobalGroups.size(); i++) {
                                if (mGlobalGroups.get(i).matchToTag(tag) &&
                                        !newGroupsSearched.contains(mGlobalGroups.get(i))) {
                                    newGroupsSearched.add(mGlobalGroups.get(i));
                                }
                            }
                        }

                        YieldsApplication.setGroupsSearched(newGroupsSearched);

                        notifyChange();
                    }
                }, 2000);
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
    public void notifyChange() {

        // Need to run on the UI thread because some views are modified
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
    }

    /**
     * To be removed when the requests are operational, and the actual searched
     * groups can be fetched from the server
     */
    private void createFakeGroups(){
        mGlobalGroups = new ArrayList<>();
        Group g1 = new Group("SWENG", new Id(666), new ArrayList<User>());
        g1.addTag(new Group.Tag("hard"));
        mGlobalGroups.add(g1);

        Group g2 = new Group("Hello", new Id(667), new ArrayList<User>());
        g2.addTag(new Group.Tag("nice"));
        mGlobalGroups.add(g2);

        Group g3 = new Group("nature", new Id(668), new ArrayList<User>());
        g3.addTag(new Group.Tag("wild"));
        g3.addTag(new Group.Tag("nature"));
        mGlobalGroups.add(g3);

        Group g4 = new Group("HelloNature", new Id(668), new ArrayList<User>());
        g4.addTag(new Group.Tag("wild"));
        g4.addTag(new Group.Tag("nice"));
        mGlobalGroups.add(g4);
    }
}
