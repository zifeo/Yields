package yields.client.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.listadapter.ListAdapterSearchedGroups;
import yields.client.listadapter.ListAdapterUsers;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class NodeListActivity extends AppCompatActivity {

    private Group mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        mGroup = Objects.requireNonNull(YieldsApplication.getGroup(),
                "The group in YieldsApplication cannot be null when NodeListActivity is created");

        ListView listView = (ListView) findViewById(R.id.listViewNodes);

        ListAdapterSearchedGroups adapter = new ListAdapterSearchedGroups(getApplicationContext(),
                R.layout.group_searched_layout, mGroup.getNodes());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YieldsApplication.setGroup(mGroup.getNodes().get(position));

                Intent intent = new Intent(NodeListActivity.this, GroupInfoActivity.class);
                startActivity(intent);
            }
        });
        listView.setItemsCanFocus(true);

        actionBar.setTitle("Nodes of " + mGroup.getName());
    }

    /**
     * Automatically called when the activity is resumed
     */
    @Override
    public void onResume() {
        super.onResume();
        YieldsApplication.setGroup(mGroup);
    }
}
