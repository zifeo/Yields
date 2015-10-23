package yields.client.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import yields.client.R;
import yields.client.listadapter.ListAdapterUsers;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class CreateGroupActivity extends AppCompatActivity {
    private ListAdapterUsers mAdapterUsers;
    private List<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mUsers = new ArrayList<>();
        mUsers.add(YieldsApplication.getUser());

        mAdapterUsers = new ListAdapterUsers(getApplicationContext(), R.layout.add_user_layout, mUsers);

        ListView listView = (ListView) findViewById(R.id.listViewCreateGroup);

        listView.setAdapter(mAdapterUsers);
        listView.setItemsCanFocus(false);

        mAdapterUsers.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_create_group, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
