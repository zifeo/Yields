package yields.client.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import yields.client.R;
import yields.client.listadapter.ListAdapterGroups;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.yieldsapplication.YieldsApplication;

public class CreateGroupActivity extends AppCompatActivity {
    private ArrayAdapter<String> mArrayAdapter;
    private List<Node> mNodes;
    private List<String> mNodesName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mNodes = new ArrayList<>();
        mNodes.add(YieldsApplication.getUser());

        mNodesName = new ArrayList<>();
        for (int i = 0; i < mNodes.size(); i++){
            mNodesName.add(mNodes.get(i).getName());
        }

        mArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.add_node_layout, mNodesName);

        ListView listView = (ListView) findViewById(R.id.listViewCreateGroup);

        listView.setAdapter(mArrayAdapter);
        listView.setItemsCanFocus(false);

        mArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_create_group, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
