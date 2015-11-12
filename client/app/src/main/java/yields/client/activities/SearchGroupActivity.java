package yields.client.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import yields.client.R;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterGroups;
import yields.client.listadapter.ListAdapterSearchedGroups;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class SearchGroupActivity extends AppCompatActivity implements NotifiableActivity{
    private MenuItem mMenuSearch;
    private MenuItem mMenuClose;
    private EditText mEditTextSearch;
    private ActionBar mActionBar;
    private List<Group> mCurrentGroups;
    private List<Group> mGlobalGroups; // to be removed
    private ListAdapterSearchedGroups mAdapterCurrentGroups;

    private TextView mTextViewInfo;
    private ListView mListView;
    private ProgressBar mProgressBar;

     /*Used to not launch requests each time the user types a new character
     but rather waits a few seconds before doing it */
    private Timer mTimer = null;

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
        mListView.setVisibility(View.INVISIBLE);

        mTextViewInfo = (TextView) findViewById(R.id.textViewInfoSearch);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarSearch);
        mProgressBar.setVisibility(View.INVISIBLE);
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

        mEditTextSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (mTimer != null) {
                    mTimer.cancel();
                }

                mTimer = new Timer("DelayedRequestTimer");
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        launchSearch(mEditTextSearch.getText().toString());
                    }
                }, 1000);
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
                setWaitingState();

                mTemporaryTimer = new Timer("FakeRequestTimer");
                mTemporaryTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mCurrentGroups.clear();

                        for (int i = 0; i < mGlobalGroups.size(); i++){
                            if (mGlobalGroups.get(i).getName().startsWith(text)){
                                mCurrentGroups.add(mGlobalGroups.get(i));
                            }
                        }

                        notifyChange();
                    }
                }, 5000);
            }
        });
    }

    /**
     * This methods changes the look of the activity to 'waiting'
     */
    private void setWaitingState(){
        mTextViewInfo.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.INVISIBLE);
    }

    /**
     * This methods sets the appropriate views to visible or invisible
     * when new result is received
     */
    private void setNewResultsState(){
        mTextViewInfo.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mListView.setVisibility(View.VISIBLE);
    }

    /**
     * This methods sets the appropriate views to visible or invisible when
     * no new result is received
     */
    private void setNoResultsState(){
        mTextViewInfo.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mListView.setVisibility(View.INVISIBLE);

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
                if (mCurrentGroups.size() == 0){
                    setNoResultsState();
                }
                else {
                    setNewResultsState();
                }
                mAdapterCurrentGroups.notifyDataSetChanged();
            }
        });
    }

    // To be removed soon
    private void createFakeGroups(){
        mGlobalGroups = new ArrayList<>();
        mGlobalGroups.add(new Group("SWENG", new Id(666), new ArrayList<User>()));
        mGlobalGroups.add(new Group("Hello", new Id(667), new ArrayList<User>()));
        mGlobalGroups.add(new Group("Nature", new Id(668), new ArrayList<User>()));
        mGlobalGroups.add(new Group("HelloNature", new Id(669), new ArrayList<User>()));
    }
}
