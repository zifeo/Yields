package yields.client.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import yields.client.R;
import yields.client.exceptions.ContentException;
import yields.client.exceptions.MessageActivityException;
import yields.client.exceptions.MessageException;
import yields.client.exceptions.NodeException;
import yields.client.id.Id;

import yields.client.listadapter.ListAdapter;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity used to display messages for a group
 */
public class MessageActivity extends Activity {
    private static ClientUser mUser;
    private static Group mGroup;
    private static ArrayList<Message> mMessages;
    private static ListAdapter mAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap mImage; // Image taken from the gallery.
    private boolean mSendImage;

    /**
     * Starts the activity by displaying the group info and showing the most recent
     * messages.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        YieldsApplication.setApplicationContext(getApplicationContext());
        YieldsApplication.setResources(getResources());

        /*mUser = YieldsApplication.getUser();
        mGroup = YieldsApplication.getGroup();*/

        /** FOR SAKE OF SPRINT PRESENTATION !!! **/
        try {
            Bitmap image1 = BitmapFactory.decodeResource(YieldsApplication.getResources(), R.drawable.userpicture);
            mUser = new MockClientUser("Mock User", new Id(117), "Mock Email", image1);
            mGroup = createFakeGroup();
        } catch (NodeException e) {
            e.printStackTrace();
        }

        mMessages = new ArrayList<>();
        mImage = null;
        mSendImage = false;


        mAdapter = new ListAdapter(YieldsApplication.getApplicationContext(), R.layout.messagelayout,
                mMessages);
        ListView listView = (ListView) findViewById(R.id.messageScrollLayout);
        listView.setAdapter(mAdapter);

        if(mUser == null || mGroup == null) {
            int duration = Toast.LENGTH_SHORT;
            TextView groupName = (TextView) findViewById(R.id.groupName);
            groupName.setText("Unknown group");
            Toast toast = Toast.makeText(this, "Impossible to load group info", duration);
            toast.show();
        }else {
            setHeaderBar();
            retrieveGroupMessages();
        }
    }

    /**
     * Listener called when the user sends a message to the group.
     */
    public void onSendMessage(View v) throws MessageActivityException {
        TextView inputField = (TextView) findViewById(R.id.inputMessageField);
        String inputMessage =  inputField.getText().toString();

        inputField.setText("");
        Content content;
        if (mSendImage){
            if (mImage == null){
                throw new MessageActivityException("Error, attempting to send a null image.");
            }
            try {
                content = new ImageContent(mImage, inputMessage);
            } catch (ContentException e) {
                throw new MessageActivityException("Error im message activity, couldn't create ImageContent.");
            }
            mSendImage = false;
        }
        else {
            content = new TextContent(inputMessage);
        }
        Message message = null;
        try {
            message = new Message("message", new Id(1230), mUser, content);
        } catch (MessageException e) {
            throw new MessageActivityException("Error in message activity, couldn't create message");
        } catch (NodeException e) {
            throw new MessageActivityException("Error in message activity, couldn't create message");
        }
        // TODO : take right name and right id.
        mMessages.add(message);
        //mUser.sendMessage(mGroup, message); TODO : implement sendMessage for ClientUser.
        mAdapter.notifyDataSetChanged();
        ListView lv = (ListView) findViewById(R.id.messageScrollLayout);
        lv.setSelection(lv.getAdapter().getCount() - 1);
    }

    /**
     * Listener called when the user presses the picture icon.
     * @param v The view which called this method
     */
    public void onClickAddImage(View v){
        mSendImage = true;
        pickImageFromGallery();
    }

    /**
     * Called when new message(s) have been received.
     * @param newMessages The new message(s) received.
     */
    public void receiveNewMessage(List<Message> newMessages){
        mMessages.addAll(newMessages);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Retrieve message from the server and puts them in the mMessages attribute.
     */
    private void retrieveGroupMessages(){
        for(Message m : mUser.getGroupMessages(mGroup)){
            mMessages.add(m);
        }
        mAdapter.notifyDataSetChanged();
        ListView lv = (ListView) findViewById(R.id.messageScrollLayout);
        lv.setSelection(lv.getAdapter().getCount() - 1);
    }

    /**
     * Starts the activity which allows the user to pick which image from his gallery
     * he wants to send.
     */
    private void pickImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Sets the correct information on the header.
     */
    private void setHeaderBar(){
        TextView groupNameField = (TextView) findViewById(R.id.groupName);
        groupNameField.setText(mGroup.getName());
    }

    /**
     * Is called once the image picking is finished. It displays a toast informing the
     * user that he added a message to his message.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                mImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Toast toast = Toast.makeText(YieldsApplication.getApplicationContext(), "Image added to message", Toast.LENGTH_SHORT);
                toast.show();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * Mock Client user, only for presentation during the second sprint.
     */
    private class  MockClientUser extends ClientUser{

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
            /*
            Message message1 = null;
            Message message2 = null;
            Message message3 = null;
            Bitmap image1 = BitmapFactory.decodeResource(YieldsApplication.getResources(), R.drawable.userpicture);
            Bitmap image2 = BitmapFactory.decodeResource(YieldsApplication.getResources(), R.drawable.jbouron);
            Bitmap image3 = BitmapFactory.decodeResource(YieldsApplication.getResources(), R.drawable.tstocco);


            try {
                User user1 = new MockClientUser("Teo Stocco", new Id(1), "lol@jpg.com", image3);
                Content content1 = new TextContent("This app is going to be sick ! Look at how "+
                        "fast we're making progress !");
                message1 = new Message("lol", new Id(1), user1, content1);
                User user2 = new MockClientUser("Nicolas Roussel", new Id(1), "lol@jpg.com", image1);
                Content content2 = new TextContent("Well, actually I can't really work, "+
                " because I'm not sure the app will run on any given device.");
                message2 = new Message("lol", new Id(1), user2, content2);
                User user3 = new MockClientUser("Justinien Bouron", new Id(1), "lol@jpg.com", image2);
                Content content3 = new TextContent("If only we could use Jenkins :/");
                message3 = new Message("lol", new Id(1), user3, content3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageList.add(message1);
            messageList.add(message2);
            messageList.add(message3);*/
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
        public Map<User, String> getHistory(Date from) {
            return null;
        }
    }

    /**
     * Create fake group for sake of the presentation.
     * @return fake group.
     */
    private Group createFakeGroup() throws NodeException {
        return new Group("Mock group", new Id(123), new ArrayList<Node>());
    }
}
