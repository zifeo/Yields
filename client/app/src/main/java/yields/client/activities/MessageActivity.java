package yields.client.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ExecutionException;

import yields.client.R;
import yields.client.exceptions.MessageActivityException;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterMessages;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.service.MessageBinder;
import yields.client.service.YieldService;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity used to display messages for a group
 */
public class MessageActivity extends AppCompatActivity implements NotifiableActivity {
    private static ClientUser mUser;
    private static Group mGroup;
    private static ArrayList<Message> mMessages;
    private static ListAdapterMessages mAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap mImage; // Image taken from the gallery.
    private boolean mSendImage;
    private static EditText mInputField;
    private static ListView mMessageScrollLayout;
    private static MessageBinder mMessageBinder;

    private ActionBar mActionBar;

    /**
     * Starts the activity by displaying the group info and showing the most recent
     * messages.
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO set disabled send button

        setContentView(R.layout.activity_message);
        YieldsApplication.setApplicationContext(getApplicationContext());
        YieldsApplication.setResources(getResources());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mUser = YieldsApplication.getUser();
        mGroup = YieldsApplication.getGroup();

        mMessages = new ArrayList<>();
        mImage = null;
        mSendImage = false;

        mAdapter = new ListAdapterMessages(YieldsApplication.getApplicationContext(), R.layout.messagelayout,
                mMessages);
        mMessageScrollLayout = (ListView) findViewById(R.id.messageScrollLayout);
        mMessageScrollLayout.setAdapter(mAdapter);

        mInputField = (EditText) findViewById(R.id.inputMessageField);

        if(mUser == null || mGroup == null) {
            showErrorToast("Couldn't get group information.");
            mActionBar.setTitle("Unknown group");
        } else {
            setHeaderBar();
            try {
                new RetrieveMessageTask().execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        Intent serviceIntent = new Intent(this, YieldService.class)
                .putExtra("bindMessageActivity", true);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * @return The id of the current group
     */
    public Id getGroupId() {
        return mGroup.getId();
    }

    /**
     * Method automatically called for the tool bar items
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_message, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Listener called when the user sends a message to the group.
     */
    public void onSendMessage(View v) throws MessageActivityException, IOException {
        String inputMessage =  mInputField.getText().toString();

        mInputField.setText("");
        Content content;
        if (mSendImage && mImage != null){
            content = new ImageContent(mImage, inputMessage);
            mSendImage = false;
            mImage = null;
        }
        else {
            content = new TextContent(inputMessage);
        }
        Message message = new Message("message", new Id(1230), mUser, content, new Date(), mGroup);
        // TODO : take right name and right id.
        mMessages.add(message);
        mUser.sendMessage(mGroup, message);

        mAdapter.notifyDataSetChanged();
        mMessageScrollLayout.setSelection(mMessageScrollLayout.getAdapter().getCount() - 1);
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
                if (mImage != null) {
                    Toast toast = Toast.makeText(YieldsApplication.getApplicationContext(), "Image added to message", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (IOException e) {
                Log.d("Message Activity", "Couldn't add image to the message");
            }
        }
    }

    /** Method used to take care of clicks on the tool bar
     *
     * @param item The tool bar item clicked
     * @return true iff the click is not propagated
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionSettingsGroup:
                Intent intent = new Intent(this, GroupSettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

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
        SortedMap<Date, Message> messagesTree = mGroup.getLastMessages();

        for(Message message : messagesTree.values()){
            mMessages.add(message);
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
        mActionBar.setTitle(mGroup.getName());
    }

    /**
     * Notify the activity that the
     * data set has changed
     */
    @Override
    public void notifyChange() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Retrieve the group messages.
     */
    private class RetrieveMessageTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                retrieveGroupMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessageBinder = (MessageBinder) service;
            //TODO activate send button
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //TODO send Toast
        }
    };
}
