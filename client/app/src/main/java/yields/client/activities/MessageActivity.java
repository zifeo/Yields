package yields.client.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import yields.client.R;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class MessageActivity extends AppCompatActivity {

    private static ClientUser mUser;
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

        retrieveGroupMessages();
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
        mMessages = new ArrayDeque<>(mUser.getGroupMessages(mGroup));
    }

    /**
     * Displays the messages contained in mMessages on the layout.
     */
    private void displayMessages(){
        ScrollView messagesScrollView = (ScrollView) findViewById(R.id.messagesScrollView);

        Iterator<Message> iterator = mMessages.iterator();
        Message nextMessage;
        while (iterator.hasNext()){
            nextMessage = iterator.next();
            View messageView = nextMessage.getMessageView();
            messagesScrollView.addView(messageView);
        }
    }
}
