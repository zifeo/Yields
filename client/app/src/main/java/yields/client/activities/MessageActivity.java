package yields.client.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

        //setTitle(mGroup.getName());

        //retrieveGroupMessages();
        YieldsApplication.setApplicationContext(getApplicationContext());
        displayMessages();
    }

    /**
     * Listener called when the user sends a message to the group.
     */
    public void onSendTextMessage(View v){
        TextView inputField = (TextView) findViewById(R.id.inputMessageField);
        String inputMessage =  inputField.getText().toString();
        TextContent content = new TextContent(inputMessage);
        Message message = new Message(null, mUser, content);
        mMessages.addLast(message);
       // mUser.sendMessage(mGroup, message); TODO : implement sendMessage for ClientUser.
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
        mMessages = new ArrayDeque<>(mUser.getGroupMessages(mGroup));
    }

    /**
     * Displays the messages contained in mMessages on the layout.
     */
    private void displayMessages(){
        LinearLayout  messagesScrollView = (LinearLayout) findViewById(R.id.messageScrollLayout);
        messagesScrollView.removeAllViews();
        Iterator<Message> iterator = mMessages.iterator();
        Message nextMessage;
        while (iterator.hasNext()){
            nextMessage = iterator.next();
            TextView tv = new TextView(YieldsApplication.getApplicationContext());
            tv.setText(((TextContent) nextMessage.getContent()).getText());
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(20);
            messagesScrollView.addView(tv);
        }
    }
}
