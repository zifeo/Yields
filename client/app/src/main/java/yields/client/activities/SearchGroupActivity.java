package yields.client.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import yields.client.R;

public class SearchGroupActivity extends AppCompatActivity implements NotifiableActivity{
    private MenuItem mMenuSearch;
    private MenuItem mMenuClose;
    private EditText mEditTextSearch;
    private ActionBar mActionBar;

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
        Intent intent = null;

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

        mActionBar.setDisplayShowCustomEnabled(true); //enable it to display a
        // custom view in the action bar.
        mActionBar.setCustomView(R.layout.search_bar_layout);//add the custom view

        mEditTextSearch = (EditText)mActionBar.getCustomView().findViewById(R.id.editTextSearch); //the text editor

        //this is a listener to do a search when the user clicks on search button
        mEditTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //doSearch();
                    return true;
                }
                return false;
            }
        });


        mEditTextSearch.requestFocus();

        //open the keyboard focused in the edtSearch
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTextSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Called when the user clicks on the 'Close Search' button
     */
    private void closeSearch(){
        mMenuSearch.setVisible(true);
        mMenuClose.setVisible(false);
    }

    /**
     * Notify the activity that the
     * data set has changed
     */
    @Override
    public void notifyChange() {

    }
}
