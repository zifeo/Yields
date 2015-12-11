package yields.client.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import yields.client.R;
import yields.client.listadapter.ListAdapterUsersCheckBox;
import yields.client.node.User;
import yields.client.servicerequest.ServiceRequest;
import yields.client.servicerequest.UserEntourageRemoveRequest;
import yields.client.yieldsapplication.YieldsApplication;

public class RemoveUsersFromEntourageActivity extends AppCompatActivity {
    private ListAdapterUsersCheckBox mAdapter;
    private List<Map.Entry<User, Boolean>> mEntourageChecked;

    /**
     * Method automatically called on the creation of the activity
     *
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_users_from_entourage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mEntourageChecked = new ArrayList<>();
        List<User> entourage = YieldsApplication.getUser().getEntourage();

        for (int i = 0; i < entourage.size(); i++) {
            mEntourageChecked.add(new AbstractMap.SimpleEntry<>(entourage.get(i), false));
        }

        mAdapter = new ListAdapterUsersCheckBox(getApplicationContext(),
                R.layout.add_user_layout, mEntourageChecked, false);

        ListView listView = (ListView) findViewById(R.id.listViewCreateGroupSelectUsers);
        listView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    /**
     * Method automatically called for the tool bar items
     *
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_remove_users_from_entourage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method called when the user clicks on 'Done'
     *
     * @param item The tool bar item clicked
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final List<User> usersToBeRemoved = new ArrayList<>();
        for (int i = 0; i < mEntourageChecked.size(); i++) {
            if (mEntourageChecked.get(i).getValue()) {
                usersToBeRemoved.add(mEntourageChecked.get(i).getKey());
            }
        }

        if (usersToBeRemoved.size() > 0) {
            String title = "Remove users from entourage";
            if (usersToBeRemoved.size() == 1) {
                title = "Remove " + usersToBeRemoved.get(0).getName() + " from your entourage";
            }

            String message = "Are you sure you want to remove these users from your entourage ?";
            if (usersToBeRemoved.size() == 1) {
                message = "Are you sure you want to remove " + usersToBeRemoved.get(0).getName() +
                        " from your entourage ?";
            }

            AlertDialog dialog = new AlertDialog.Builder(RemoveUsersFromEntourageActivity.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            for (User u : usersToBeRemoved) {
                                YieldsApplication.getUser().removeUserFromEntourage(u);

                                ServiceRequest request =
                                        new UserEntourageRemoveRequest(YieldsApplication.getUser().getId(), u.getId());

                                YieldsApplication.getBinder().sendRequest(request);
                            }

                            YieldsApplication.showToast(getApplicationContext(),
                                    usersToBeRemoved.size() + " user(s) removed");

                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            dialog.show();
        } else {
            finish();
        }

        return true;
    }
}
