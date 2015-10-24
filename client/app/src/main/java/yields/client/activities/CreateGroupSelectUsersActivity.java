package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import yields.client.R;
import yields.client.gui.PairUserBoolean;
import yields.client.listadapter.ListAdapterUsersCheckBox;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class CreateGroupSelectUsersActivity extends AppCompatActivity {
    private ListAdapterUsersCheckBox mAdapterEntourage;
    private List<PairUserBoolean> mEntourageChecked;
    private ListView mListView;

    public static final String EMAIL_LIST_KEY = "EMAIL_LIST";
    public static final String EMAIL_LIST_INPUT_KEY = "EMAIL_LIST_INPUT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_select_users);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        ArrayList<String> inputEmailList = getIntent().getStringArrayListExtra(EMAIL_LIST_INPUT_KEY);

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
        //mListView.setItemsCanFocus(false);

        mAdapterEntourage.notifyDataSetChanged();
    }

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

        Log.i("Kapoue", " Size : " + emailList.size());

        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra(EMAIL_LIST_KEY, emailList);

        setResult(RESULT_OK, returnIntent);
        finish();

        return true;
    }
}
