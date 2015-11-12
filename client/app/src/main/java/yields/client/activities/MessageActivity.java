package yields.client.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import yields.client.R;
import yields.client.exceptions.NodeException;
import yields.client.fragments.CommentFragment;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterMessages;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.service.MessageBinder;
import yields.client.service.YieldService;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity used to display messages for a group
 */
public class MessageActivity extends AppCompatActivity implements NotifiableActivity {
    public enum ContentType {GROUP_MESSAGES, MESSAGE_COMMENTS}

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
    private static ActionBar mActionBar;
    private static ImageButton mSendButton;

    private static Fragment mFragment;
    private static ContentType mType;
    private static Message mCommentMessage;

    /**
     * Starts the activity by displaying the group info and showing the most recent
     * messages.
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);
        YieldsApplication.setApplicationContext(getApplicationContext());
        YieldsApplication.setResources(getResources());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // TODO : Uncoomment for tests !!!
        /*
        mUser = YieldsApplication.getUser();
        mGroup = YieldsApplication.getGroup();
        */

        mUser = new FakeUser("Bob Ross", new Id(2), "topkek", Bitmap
                .createBitmap(80, 80, Bitmap.Config.RGB_565));
        mGroup = new FakeGroup("Mock Group", new Id(2), new ArrayList<User>(),
                Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565), Group
                        .GroupVisibility.PUBLIC, true);

        mMessages = new ArrayList<>();
        mImage = null;
        mSendImage = false;

        mAdapter = new ListAdapterMessages(YieldsApplication.getApplicationContext(), R.layout.messagelayout,
                mMessages);
       /* mMessageScrollLayout = (ListView) findViewById(R.id
                .messageScrollLayout);*/
        mMessageScrollLayout = new ListView(YieldsApplication
                .getApplicationContext());
        mMessageScrollLayout.setAdapter(mAdapter);

        mInputField = (EditText) findViewById(R.id.inputMessageField);

        if(mUser == null || mGroup == null) {
            String message = "Couldn't get group information.";
            YieldsApplication.showToast(getApplicationContext(), message);
            mActionBar.setTitle("Unknown group");
        } else {
            setHeaderBar();
        }

        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mSendButton.setEnabled(false);

        // TODO :
        mType = ContentType.GROUP_MESSAGES;
        createFragment();
    }

    /**
     * Automatically called when the activity is resumed after another activity was displayed
     */
    @Override
    public void onResume(){
        super.onResume();

        Intent serviceIntent = new Intent(this, YieldService.class)
                .putExtra("bindMessageActivity", true);

        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called to pause the activity
     */
    @Override
    public void onPause(){
        super.onPause();

        unbindService(mConnection);
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
    public void onSendMessage(View v){
        if (mType == ContentType.GROUP_MESSAGES){
            ((ListFragment) mFragment).getListView().setOnItemClickListener
                    (new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mCommentMessage = (Message) ((ListFragment)
                                    mFragment)
                                    .getListView().getAdapter().getItem(0);
                            launchCommentFragment();
                        }
                    });
        }
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
        Message message = new Message("message", new Id(1230), mUser, content, new Date());
        // TODO : take right name and right id.
        mMessages.add(message);
        //mMessageBinder.sendMessage(mGroup, message);

        mAdapter.notifyDataSetChanged();
        mMessageScrollLayout.setSelection(mMessageScrollLayout.getAdapter()
                .getCount() - 1);
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
                    String message = "Image added to message";
                    YieldsApplication.showToast(getApplicationContext(), message);
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
     * Notify the activity that the
     * data set has changed.
     */
    @Override
    public void notifyChange() {
        retrieveGroupMessages();
    }

    public ListView getCurrentFragmentListView(){
        if (mType == ContentType.GROUP_MESSAGES) {
            return ((ListFragment) mFragment).getListView();
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    private void launchCommentFragment(){
        mType = ContentType.MESSAGE_COMMENTS;
        createFragment();
    }

    private void createFragment(){
        if (mType == ContentType.GROUP_MESSAGES) {
            mActionBar.setTitle(mGroup.getName());
            mFragment = new ListFragment();
            ((ListFragment) mFragment).setListAdapter(mAdapter);
        }
        else{
            mActionBar.setTitle("Message from " + mCommentMessage.getSender()
                    .getName());
            mFragment = new CommentFragment();
            ((CommentFragment) mFragment).setAdapter(mAdapter);
            ((CommentFragment) mFragment).setMessage(mCommentMessage);
            mAdapter.notifyDataSetChanged();
        }
        Log.d("MessageActivity", "Fragment created");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frgLayout, mFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Retrieve message from the server and puts them in the mMessages attribute.
     */
    private void retrieveGroupMessages() {
        SortedMap<Date, Message> messagesTree = mGroup.getLastMessages();

        for(Message message : messagesTree.values()){
            mMessages.add(message);
        }
        mAdapter.notifyDataSetChanged();
        mMessageScrollLayout.setSelection(mMessageScrollLayout.getAdapter().getCount() - 1);
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

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*mMessageBinder = (MessageBinder) service;
            mMessageBinder.attachActivity(MessageActivity.this);*/
            mSendButton.setEnabled(true);
            /*mMessageBinder.addMoreGroupMessages(mGroup, new java.util.Date()
                    , 20);*/
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessageBinder = null;
            mSendButton.setEnabled(false);
        }
    };

    private class  FakeUser extends ClientUser{

        public FakeUser(String name, Id id, String email, Bitmap img) throws NodeException {
            super(name, id, email, img);
        }

        @Override
        public void sendMessage(Group group, Message message) throws IOException {

        }

        @Override
        public List<Message> getGroupMessages(Group group, Date lastDate) throws IOException {
            return null;
        }

        @Override
        public void createNewGroup(Group group) throws IOException {

        }

        @Override
        public void deleteGroup(Group group) {

        }

        @Override
        public Map<User, String> getHistory(Group group, Date from) {
            return null;
        }
    }

    private class FakeGroup extends Group{

        public FakeGroup(String name, Id id, List<User> users, Bitmap image, GroupVisibility visibility, boolean validated) {
            super(name, id, users, image, visibility, validated);
        }
    }
}
