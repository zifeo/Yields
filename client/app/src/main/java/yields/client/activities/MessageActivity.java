package yields.client.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class MessageActivity extends Activity {
    private static ClientUser mUser;
    private static Group mGroup;
    private static ArrayList<Message> mMessages;
    private static ListAdapter mAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap mImage; // Image taken from the gallery.
    private boolean mSendImage;

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
            mUser = new MockClientUser("Mock User", new Id(117), "Mock Email");
        } catch (NodeException e) {
            e.printStackTrace();
        }
        try {
            mGroup = createFakeGroup();
        } catch (NodeException e) {
            e.printStackTrace();
        }
        /****/

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

    private void pickImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void setHeaderBar(){
        TextView groupNameField = (TextView) findViewById(R.id.groupName);
        groupNameField.setText(mGroup.getName());
    }

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

        public MockClientUser(String name, Id id, String email) throws NodeException {
            super(name, id, email);
        }

        @Override
        public void sendMessage(Group group, Message message) {
            /* Nothing */
        }

        @Override
        public List<Message> getGroupMessages(Group group) {
            return new ArrayList<>();
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
        return new Group("Mock group", new Id(123), new ArrayList<User>());
    }
}
