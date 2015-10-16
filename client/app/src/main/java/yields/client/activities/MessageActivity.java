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
        /* TODO : update message deque, send message to the server. */
    }

    /**
     * Retrieve message from the server and puts them in the mMessages attribute.
     */
    private void retrieveGroupMessages(){
        /* TODO : call the right method to get message from the server and put them in the
            mMessaage deque. */
        /* For now it fills mMessages with  mock messages. */

    }
}
