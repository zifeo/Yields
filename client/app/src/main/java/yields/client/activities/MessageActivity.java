package yields.client.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import yields.client.R;
import yields.client.exceptions.MessageActivityException;
import yields.client.exceptions.MessageViewException;
import yields.client.exceptions.NodeException;
import yields.client.id.Id;

import yields.client.listadapter.ListAdapterMessages;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity used to display messages for a group
 */
public class MessageActivity extends Activity {
    private static ClientUser mUser;
    private static Group mGroup;
    private static ArrayList<Message> mMessages;
    private static ListAdapterMessages mAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap mImage; // Image taken from the gallery.
    private boolean mSendImage;
    private static EditText mInputField;

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

        mUser = YieldsApplication.getUser();
        mGroup = YieldsApplication.getGroup();

        mMessages = new ArrayList<>();
        mImage = null;
        mSendImage = false;

        mAdapter = new ListAdapterMessages(YieldsApplication.getApplicationContext(), R.layout.messagelayout,
                mMessages);
        ListView listView = (ListView) findViewById(R.id.messageScrollLayout);
        listView.setAdapter(mAdapter);

        mInputField = (EditText) findViewById(R.id.inputMessageField);

        if(mUser == null || mGroup == null) {
            showErrorToast("Couldn't get group informations.");
            TextView groupName = (TextView) findViewById(R.id.groupName);
            groupName.setText("Unknown group");
        }else {
            setHeaderBar();
            try {
                retrieveGroupMessages();
            } catch (IOException e) {
                showErrorToast("Couldn't retrieve messages.");
            }
        }
    }

    /**
     * Listener called when the user sends a message to the group.
     */
    public void onSendMessage(View v) throws MessageActivityException {
        String inputMessage =  mInputField.getText().toString();

        mInputField.setText("");
        Content content;
        if (mSendImage){
            content = new ImageContent(mImage, inputMessage);
            mSendImage = false;
        }
        else {
            content = new TextContent(inputMessage);
        }
        Message message = new Message("message", new Id(1230), mUser, content);
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
                Log.d("Message Activity", "Couldn't add image to the message");
            }
        }
    }

    /**
     * Show an error message in a toast.
     * @param errorMsg The error message to be displayed.
     */
    private void showErrorToast(String errorMsg){
        Toast toast = Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Retrieve message from the server and puts them in the mMessages attribute.
     */
    private void retrieveGroupMessages() throws IOException {
        SortedMap<Date, Message> messagesTree = mGroup.getMessages();
        for(Map.Entry<Date, Message> entry : messagesTree.entrySet()){
            mMessages.add(entry.getValue());
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
}
