package yields.client.activities;

import android.app.ListActivity;
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

        mAdapterGroups.notifyDataSetChanged();
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
                intent = new Intent(this, CreateGroupActivity.class);
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
        group1.addMessage(new Message("", new Id(667), YieldsApplication.getUser(), new TextContent("Nice to see you !")));
        group1.addMessage(new Message("", new Id(668), YieldsApplication.getUser(), new TextContent("You too !")));
        mGroups.add(group1);

        Group group2 = new Group("Answer to the Universe", new Id(42), new ArrayList<User>());
        group2.addMessage(new Message("", new Id(43), YieldsApplication.getUser(), new TextContent("42 ?")));
        group2.addMessage(new Message("", new Id(44), YieldsApplication.getUser(), new TextContent("42 !")));
        mGroups.add(group2);
    }

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
            /* Nothing */
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
