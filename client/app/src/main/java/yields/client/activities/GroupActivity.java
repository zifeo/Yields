package yields.client.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import yields.client.R;
import yields.client.exceptions.NodeException;
import yields.client.gui.GraphicTransforms;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterGroups;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class GroupActivity extends AppCompatActivity {
    private ListAdapterGroups mAdapterGroups;
    private List<Group> mGroups;

    /* String used for debug log */
    private static final String TAG = "GroupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);


        mAdapterGroups = new ListAdapterGroups(getApplicationContext(), R.layout.group_layout, mGroups);

        ListView listView = (ListView) findViewById(R.id.listViewGroups);

        listView.setAdapter(mAdapterGroups);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YieldsApplication.setGroup(mGroups.get(position));

                Intent intent = new Intent(GroupActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });
        listView.setItemsCanFocus(false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Method used to take care of clicks on the tool bar
     *
     * @param item The tool bar item clicked
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.actionEntourage:
                intent = new Intent(this, CreatingAccountActivity.class);
            break;

            case R.id.actionDiscover:
                intent = new Intent(this, CreatingAccountActivity.class);
            break;

            case R.id.actionCreate:
                intent = new Intent(this, CreateGroupSelectNameActivity.class);
            break;

            case R.id.actionSettings:
                intent = new Intent(this, CreatingAccountActivity.class);
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        if (intent == null){
            Log.d(TAG, "Error : intent should never be null at this point"); // maybe throw exception
        }
        else {
            startActivity(intent);
        }

        return intent == null;
    }


    @Override
    protected void onStart(){
        super.onStart();

        mAdapterGroups.notifyDataSetChanged();
    }

    /**
     * To be removed as soon as the logging is working
     */

    private class MockClientUser extends ClientUser {

        public MockClientUser(String name, Id id, String email, Bitmap img) throws NodeException {
            super(name, id, email, img);
        }

        @Override
        public void sendMessage(Group group, Message message) {
            /* Nothing */
        }

        @Override
        public List<Message> getGroupMessages(Group group) {
            ArrayList<Message> messageList =  new ArrayList<>();
            return messageList;
        }

        @Override
        public void addNewGroup(Group group) {
            mGroups.add(group);
            mAdapterGroups.notifyDataSetChanged();
        }

        @Override
        public void deleteGroup(Group group) {
            /* Nothing */
        }

        @Override
        public Map<User, String> getHistory(Group group, Date from) {
            return null;
        }
    }
}
