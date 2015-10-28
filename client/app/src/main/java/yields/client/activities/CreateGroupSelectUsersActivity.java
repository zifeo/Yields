package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import yields.client.R;
import yields.client.exceptions.MissingIntentExtraException;
import yields.client.gui.PairUserBoolean;
import yields.client.listadapter.ListAdapterUsersCheckBox;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the list of contacts of the user is displayed and he can choose whether
 * to include any other user in the future group
 */
public class CreateGroupSelectUsersActivity extends AppCompatActivity {
    private ListAdapterUsersCheckBox mAdapterEntourage;
    private List<PairUserBoolean> mEntourageChecked;
    private ListView mListView;

    public static final String EMAIL_LIST_KEY = "EMAIL_LIST";
    public static final String EMAIL_LIST_INPUT_KEY = "EMAIL_LIST_INPUT";

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_select_users);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        Intent intent = getIntent();

        if (!intent.hasExtra(EMAIL_LIST_INPUT_KEY)){
            throw new MissingIntentExtraException(
                    "Email list extra is missing from intent in CreateGroupSelectUsersActivity");
        }

        ArrayList<String> inputEmailList = intent.getStringArrayListExtra(EMAIL_LIST_INPUT_KEY);

        mEntourageChecked = new ArrayList<>();
        List<User> entourage = YieldsApplication.getUser().getEntourage();

        for (int i = 0; i < entourage.size(); i++){
            mEntourageChecked.add(new PairUserBoolean(entourage.get(i),
                    inputEmailList.contains(entourage.get(i).getEmail())));
        }

        mAdapterEntourage = new ListAdapterUsersCheckBox(getApplicationContext(),
                R.layout.add_user_layout, mEntourageChecked, false);

        mListView = (ListView) findViewById(R.id.listViewCreateGroupSelectUsers);

        mListView.setAdapter(mAdapterEntourage);

        mAdapterEntourage.notifyDataSetChanged();
    }

    /**
     * Method automatically called for the tool bar items
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_create_group_select_users, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Method called when the user clicks on 'Done'
     * @param item The tool bar item clicked
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<String> emailList = new ArrayList<>();

        for (int i = 0; i < mEntourageChecked.size(); i++){
            if (mEntourageChecked.get(i).getBoolean()){
                emailList.add(mEntourageChecked.get(i).getUser().getEmail());
            }
        }

        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra(EMAIL_LIST_KEY, emailList);

        setResult(RESULT_OK, returnIntent);
        finish();

        return true;
    }
}
