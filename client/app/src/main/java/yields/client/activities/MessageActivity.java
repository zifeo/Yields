package yields.client.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import yields.client.R;
import yields.client.listadapter.ListAdapter;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class MessageActivity extends AppCompatActivity {

    private static ClientUser mUser;
    private static Group mGroup;
    private static ArrayList<Message> mMessages;
    private static ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        YieldsApplication.setApplicationContext(getApplicationContext());
        YieldsApplication.setResources(getResources());

        mUser = YieldsApplication.getUser();
        mGroup = YieldsApplication.getGroup();
        mMessages = new ArrayList<>();
        mAdapter = new ListAdapter(YieldsApplication.getApplicationContext(), R.layout.messagelayout, mMessages);

        ListView lv = (ListView) findViewById(R.id.messageScrollLayout);
        lv.setAdapter(mAdapter);

        //setTitle(mGroup.getName());

        //retrieveGroupMessages();

        displayMessages();
    }

    /**
     * Listener called when the user sends a message to the group.
     */
    public void onSendTextMessage(View v){
        TextView inputField = (TextView) findViewById(R.id.inputMessageField);
        String inputMessage =  inputField.getText().toString();
        inputField.setText("");
        TextContent content = new TextContent(inputMessage);
        Message message = new Message("message", 1230, mUser, content, new Date());
                // TODO : take tight name and right id.
        mMessages.add(message);
       // mUser.sendMessage(mGroup, message); TODO : implement sendMessage for ClientUser.
        mAdapter.notifyDataSetChanged();
        displayMessages();
    }

    /**
     * Called when new message(s) have been received.
     * @param newMessages The new message(s) received.
     */
    public void receiveNewMessage(List<Message> newMessages){
        mMessages.addAll(newMessages);
        displayMessages();
    }

    /**
     * Retrieve message from the server and puts them in the mMessages attribute.
     */
    private void retrieveGroupMessages(){
        mMessages = new ArrayList<>(mUser.getGroupMessages(mGroup));
    }

    /**
     * Displays the messages contained in mMessages on the layout.
     */
    private void displayMessages(){
        /*ListView messagesScrollView = (ListView) findViewById(R.id.messageScrollLayout);
        Iterator<Message> iterator = mMessages.iterator();
        //messagesScrollView.setAdapter(new ListAdapter(YieldsApplication.getApplicationContext(), R.layout.messagelayout));
        Message nextMessage;
        LinearLayout messageLayout;
        User lastSender = null;
        User nextSender;
        ArrayList<Message> list = new ArrayList<>();
        while (iterator.hasNext()){
            nextMessage = iterator.next();
            nextSender = nextMessage.getSender();
            if (lastSender != null && lastSender.getId().equals(nextSender.getId())){
                messageLayout = nextMessage.getMessageView(true);
            }
            else{
                messageLayout = nextMessage.getMessageView(true);
            }
            lastSender = nextSender;

        }
        /*ListAdapter adapter = new ListAdapter(YieldsApplication.getApplicationContext(), R.layout.messagelayout, list);

        messagesScrollView.setAdapter(adapter);*/
    }
}
