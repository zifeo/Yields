package yields.client.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import yields.client.R;
import yields.client.exceptions.NodeException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        try {
            YieldsApplication.setUser(new MockClientUser("Mock User", new Id(117), "Mock Email", null));
        } catch (NodeException e) {
            e.printStackTrace();
        }

        mGroups = new ArrayList<>();

        Group group1 = new Group("SWENG", new Id(666), new ArrayList<Node>());
        group1.addMessage(new Message("", new Id(667), YieldsApplication.getUser(), new TextContent("Nice to see you !")));
        group1.addMessage(new Message("", new Id(668), YieldsApplication.getUser(), new TextContent("You too !")));
        mGroups.add(group1);

        Group group2 = new Group("Answer to the Ultimate Question of Life, the Universe, and Everything", new Id(42), new ArrayList<Node>());
        group2.addMessage(new Message("", new Id(43), YieldsApplication.getUser(), new TextContent("42 ?")));
        group2.addMessage(new Message("", new Id(44), YieldsApplication.getUser(), new TextContent("42 !")));
        mGroups.add(group2);

        mAdapterGroups = new ListAdapterGroups(getApplicationContext(), R.layout.group_layout, mGroups);

        ((ListView) findViewById(R.id.listViewGroups)).setAdapter(mAdapterGroups);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_group, menu);
        return super.onCreateOptionsMenu(menu);
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
