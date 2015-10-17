package yields.client.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.Date;
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
        inputField.setText("");
        TextContent content = new TextContent(inputMessage);
        Message message = new Message("message", 1230, mUser, content, new Date());
                // TODO : take tight name and right id.
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
        LinearLayout messageLayout;
        User lastSender = null;
        User nextSender;
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
            messagesScrollView.addView(messageLayout);
        }
    }
}
