package yields.client.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.io.IOException;
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
import yields.client.node.User;
import yields.client.service.YieldServiceBinder;
import yields.client.service.YieldService;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Central activity of Yields where the user can discover new nodes, create groups, see its contact
 * list, change its settings and go to chats of different groups
 */
public class GroupActivity extends NotifiableActivity {
    private ListAdapterGroups mAdapterGroups;
    private List<Group> mGroups;

    /* String used for debug log */
    private static final String TAG = "GroupActivity";

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        createFakeUserAndGroups();

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

        View connectionStatusView = findViewById(R.id.connectionStatus);

        connectionStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YieldsApplication.getBinder().reconnect();
            }
        });

        YieldsApplication.setResources(getResources());
        YieldsApplication.setApplicationContext(getApplicationContext());
    }

    /**
     * Method automatically called for the tool bar items
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_group, menu);
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
            case R.id.actionEntourage:
                intent = new Intent(this, CreatingAccountActivity.class);
            break;

            case R.id.actionDiscover:
                intent = new Intent(this, SearchGroupActivity.class);
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
            throw new IllegalStateException("Itent should never be null at this point");
        }
        else {
            startActivity(intent);
        }

        return intent != null;
    }

    /**
     * Notify the activity that the
     * data set has changed
     */
    @Override
    public void notifyChange(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapterGroups.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void notifyOnServerConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.connectionStatus).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void notifyOnServerDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.connectionStatus).setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Automatically called when the activity is started
     */
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
        public List<Message> getGroupMessages(Group group, Date lastDate) throws IOException {
            ArrayList<Message> messageList =  new ArrayList<>();
            return messageList;
        }

        @Override
        public void createNewGroup(Group group) throws IOException {
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

    /**
     * To be removed as soon as the logging is working
     */
    private void createFakeUserAndGroups() {
        Bitmap imageUser = BitmapFactory.decodeResource(getResources(), R.drawable.default_user_image);
        imageUser = GraphicTransforms.getCroppedCircleBitmap(imageUser, getResources().getInteger(R.integer.groupImageDiameter));

        try {
            YieldsApplication.setUser(new MockClientUser("Arnaud", new Id(1), "m@m.is", imageUser));
            YieldsApplication.getUser().addUserToEntourage(new MockClientUser("Nico1", new Id(2), "m@m.es", imageUser));
            YieldsApplication.getUser().addUserToEntourage(new MockClientUser("Teo", new Id(3), "m@m.fr", imageUser));
            YieldsApplication.getUser().addUserToEntourage(new MockClientUser("Justinien", new Id(4), "m@m.cn", imageUser));
            YieldsApplication.getUser().addUserToEntourage(new MockClientUser("Nico2", new Id(5), "m@m.jpp", imageUser));
            YieldsApplication.getUser().addUserToEntourage(new MockClientUser("Jeremy", new Id(6), "m@m.ch", imageUser));
        } catch (NodeException e) {
            e.printStackTrace();
        }

        Bitmap defaultGroupImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_group_image);

        int diameter = getResources().getInteger(R.integer.groupImageDiameter);
        YieldsApplication.setDefaultGroupImage(GraphicTransforms.getCroppedCircleBitmap(defaultGroupImage, diameter));

        mGroups = new ArrayList<>();

        Group group1 = new Group("SWENG", new Id(666), new ArrayList<User>());
        group1.addMessage(new Message("", new Id(667), YieldsApplication.getUser(), new TextContent("Nice to see you !"), new java.util.Date()));
        group1.addMessage(new Message("", new Id(668), YieldsApplication.getUser(), new TextContent("You too !"), new java.util.Date()));
        group1.setValidated();
        mGroups.add(group1);

        Group group2 = new Group("Answer to the Universe", new Id(42), new ArrayList<User>());
        group2.addMessage(new Message("", new Id(43), YieldsApplication.getUser(), new TextContent("42 ?"), new java.util.Date()));
        group2.addMessage(new Message("", new Id(44), YieldsApplication.getUser(), new TextContent("42 !"), new java.util.Date()));
        group2.setValidated();
        mGroups.add(group2);
    }
}
