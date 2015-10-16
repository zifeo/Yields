package yields.client.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import yields.client.R;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class MessageActivity extends AppCompatActivity {

    private static User mUser;
    private static Group mGroup;
    private static ArrayDeque<Message> mMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mUser = YieldsApplication.getUser();
        mGroup = YieldsApplication.getGroup();
        mMessages = new ArrayDeque<>();

        setTitle(mGroup.getName());
    }

    /**
     * Listener called when the user sends a message to the group.
     * @param m The message to send.
     */
    public void onSendMessage(Message m){
        mMessages.add(m);
        YieldsApplication.getUser().sendMessage(mGroup, m);
    }

    /**
     * Retrieve message from the server and puts them in the mMessages attribute.
     */
    private void retrieveGroupMessages(){
        /* TODO : call the right method to get message from the server and put them in the
            mMessaage deque. */
        /* For now it fills mMessages with  mock messages. */
        TextContent mockContent1 = new TextContent("Mock Message #1");
        TextContent mockContent2 = new TextContent("Mock Message #2");
        long mockId1 = 10;
        long mockId2 = 2;
        String mockName1 = "Mock name #1";
        String mockName2 = "Mock name #2";
        String mockEmail1 = "mock.1@mock.jpp";
        String mockEmail2 = "mock.2@mock.jpp";
        User mockUser1 = new User(mockName1, mockId1, mockEmail1);
        User mockUser2 = new User(mockName2, mockId2, mockEmail2);
        mMessages.add(new Message(mockUser1, mockContent1));
        mMessages.add(new Message(mockUser2, mockContent2));
    }

    /**
     * Displays the messages contained in mMessages on the layout.
     */
    private void displayMessages(){

    }
}
