package yields.client.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.Objects;
import java.util.SortedMap;

import yields.client.R;

import yields.client.exceptions.NodeException;
import yields.client.fragments.CommentFragment;
import yields.client.fragments.GroupMessageFragment;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterMessages;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;

import yields.client.node.User;
import yields.client.service.YieldService;
import yields.client.service.YieldServiceBinder;
import yields.client.servicerequest.GroupHistoryRequest;
import yields.client.servicerequest.NodeMessageRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity used to display messages for a group
 */
public class MessageActivity extends NotifiableActivity {
    public enum ContentType {GROUP_MESSAGES, MESSAGE_COMMENTS}

    private static ClientUser mUser;
    private static Group mGroup;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap mImage; // Image taken from the gallery.
    private boolean mSendImage;
    private static EditText mInputField;
    private static ActionBar mActionBar;
    private Menu mMenu;
    private ImageButton mSendButton;

    private static ContentType mType;
    private static Message mCommentMessage;
    private static FragmentManager mFragmentManager;
    private static Fragment mCurrentFragment;

    private static ListAdapterMessages mGroupMessageAdapter;
    private static ListAdapterMessages mCommentAdapter;


    /**
     * Starts the activity by displaying the group info and showing the most
     * recent messages.
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

        mUser = YieldsApplication.getUser();
        mGroup = YieldsApplication.getGroup();

        mImage = null;
        mSendImage = false;

        mGroupMessageAdapter = new ListAdapterMessages(YieldsApplication
                .getApplicationContext(), R.layout.messagelayoutsender,
                new ArrayList<Message>());
        mCommentAdapter = new ListAdapterMessages((YieldsApplication
                .getApplicationContext()), R.layout.messagelayoutsender, new
                ArrayList<Message>());

        mInputField = (EditText) findViewById(R.id.inputMessageField);

        if(mUser == null || mGroup == null) {
            String message = "Couldn't get group information.";
            YieldsApplication.showToast(getApplicationContext(), message);
            mActionBar.setTitle("Unknown group");
        } else {
            setHeaderBar();
        }
        mSendButton = (ImageButton) findViewById(R.id.sendButton);

        // By default, we show the messages of the group.
        mType = ContentType.GROUP_MESSAGES;
        mFragmentManager =  getFragmentManager();
        createGroupMessageFragment();

        GroupHistoryRequest historyRequest = new GroupHistoryRequest(mGroup, new Date());
        YieldsApplication.getBinder().sendRequest(historyRequest);
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
        boolean res = super.onCreateOptionsMenu(menu);
        mMenu = menu;
        YieldsApplication.getBinder().connectionStatus();
        return res;
    }

    /**
     * Listener called when the user sends a message to the group.
     */
    public void onSendMessage(View v){
        String inputMessage =  mInputField.getText().toString().trim();
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
        Message message = new Message("message", new Id(0), mUser, content,
                new Date());
        if (mType == ContentType.GROUP_MESSAGES){
            mGroupMessageAdapter.add(message);
            mGroupMessageAdapter.notifyDataSetChanged();
            NodeMessageRequest request = new NodeMessageRequest(message, mGroup);
            YieldsApplication.getBinder().sendRequest(request);
        }
        else{
            mCommentAdapter.add(message);
            mCommentAdapter.notifyDataSetChanged();
            // TODO : implement method to send comments in the message binder.
            // mYieldsServiceBinder.sendComment(...);
        }
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
     * Is called once the image picking is finished. It displays a toast
     * informing the user that he added a message to his message.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                mImage = MediaStore.Images.Media.getBitmap(getContentResolver(),
                        uri);
                if (mImage != null) {
                    String message = "Image added to message";
                    YieldsApplication.showToast(getApplicationContext(),message);
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
            case R.id.iconConnect:
                YieldsApplication.getBinder().reconnect();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                retrieveGroupMessages();
            }
        });
    }

    @Override
    public void notifyOnServerConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMenu != null) {
                    mMenu.findItem(R.id.iconConnect).setIcon(R.drawable.tick);
                }
            }
        });
    }

    @Override
    public void notifyOnServerDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMenu != null) {
                    mMenu.findItem(R.id.iconConnect).setIcon(R.drawable.cross);
                }
            }
        });
    }

    /**
     * Return the list view of the current fragment.
     * @return  If the current fragment is a groupMessageFragment then the
     * method returns the list view containing the messages (currently in
     * local memory) of the group.
     *           If the current fragment is a commentFragment then the method
     *           returns the list view containing the comments for the
     *           message the user had clicked on.
     */
    public ListView getCurrentFragmentListView(){
        if (mType == ContentType.GROUP_MESSAGES) {
            Log.d("MessageActivity", "GROUP_MESSAGE ListView");
            return ((GroupMessageFragment) mCurrentFragment)
                    .getMessageListView();
        }
        else{
            Log.d("MessageActivity", "MESSAGE_COMMENT ListView");
            return ((CommentFragment) mCurrentFragment)
                    .getCommentListView();
        }
    }

    /**
     * Getter for the fragment currently displayed.
     * @return The current fragment.
     */
    public Fragment getCurrentFragment(){
        return mCurrentFragment;
    }

    /**
     * Getter for the type of fragment.
     * @return The type of the fragment currently displayed.
     */
    public ContentType getType(){
        return mType;
    }

    /**
     * Creates a comment fragment and put it in the fragment container of the
     * MessageActivity (id fragmentPlaceHolder).
     */
    private void createCommentFragment(){
        mInputField.setText("");
        FragmentTransaction fragmentTransaction = mFragmentManager.
                beginTransaction();
        assert (mType == ContentType.MESSAGE_COMMENTS);
        mActionBar.setTitle("Message from " + mCommentMessage.getSender()
                .getName());
        mCurrentFragment = new CommentFragment();
        mCommentAdapter.clear();
        // TODO : Retrieve comments from the server using the message binder.
        ((CommentFragment) mCurrentFragment).setAdapter(mCommentAdapter);
        ((CommentFragment) mCurrentFragment).setMessage(mCommentMessage);
        Log.d("MessageActivity", "Fragment created");
        fragmentTransaction.replace(R.id.fragmentPlaceHolder, mCurrentFragment);
        fragmentTransaction.commit();
    }

    /**
     * Creates a group message fragment and put it in the fragment container of
     * the MessageActivity (id fragmentPlaceHolder).
     */
    private void createGroupMessageFragment(){
        mInputField.setText("");
        FragmentTransaction fragmentTransaction = mFragmentManager.
                beginTransaction();
        assert (mType == ContentType.GROUP_MESSAGES);
        mActionBar.setTitle(mGroup.getName());
        mCurrentFragment = new GroupMessageFragment();
        ((GroupMessageFragment) mCurrentFragment).setAdapter(mGroupMessageAdapter);
        ((GroupMessageFragment) mCurrentFragment).setMessageListOnClickListener
                (new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        mCommentMessage = mGroupMessageAdapter.
                                getItem(position);
                        mType = ContentType.MESSAGE_COMMENTS;
                        createCommentFragment();
                    }
                });
        Log.d("MessageActivity", "Fragment created");
        fragmentTransaction.replace(R.id.fragmentPlaceHolder, mCurrentFragment);
        fragmentTransaction.commit();
    }

    /**
     * Listener for the back button.
     * When the back button is pressed we need to know if we are going from a
     * comment fragment to a group message fragment or if we  are quitting
     * the activity.
     */
    @Override
    public void onBackPressed() {
        if (mType == ContentType.GROUP_MESSAGES){
            Log.d("MessageActivity", "Quit activity");
            super.onBackPressed();
        }
        else{
            Log.d("MessageActivity", "Back to group message fragment");
            mType = ContentType.GROUP_MESSAGES;
            createGroupMessageFragment();
        }
    }

    /**
     * Retrieve message from the server and puts them in the mMessages attribute.
     */
    private void retrieveGroupMessages() {
        SortedMap<Date, Message> messagesTree = mGroup.getLastMessages();

        for(Message message : messagesTree.values()){
            mGroupMessageAdapter.add(message);
        }
        mGroupMessageAdapter.notifyDataSetChanged();
    }

    /**
     * Starts the activity which allows the user to pick which image from his
     * gallery he wants to send.
     */
    private void pickImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    /**
     * Sets the correct information on the header.
     */
    private void setHeaderBar(){
        mActionBar.setTitle(mGroup.getName());
    }

    private class FakeBinder extends YieldServiceBinder {
        public FakeBinder(YieldService service) {
            super(service);
        }

        public void attachActivity(NotifiableActivity activity) {
            Log.d("MessageActivity", "Attach activity");
        }

        public void unsetMessageActivity(){
            Log.d("MessageActivity", "Attach activity");
        }

        public boolean isServerConnected(){
            return true;
        }

        public void sendRequest(ServiceRequest request) {
            Objects.requireNonNull(request);
            Log.d("MessageActivity", "Send request : " + request.getType().toString());
        }
    }

    private class  FakeUser extends ClientUser{

        public FakeUser(String name, Id id, String email, Bitmap img)
                throws NodeException {
            super(name, id, email, img);
        }

        @Override
        public void sendMessage(Group group, Message message)
                throws IOException {

        }

        @Override
        public List<Message> getGroupMessages(Group group, Date lastDate)
                throws IOException {
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

    /**
     * Private class for quick testing purposes.
     */
    private class FakeGroup extends Group{

        public FakeGroup(String name, Id id, List<User> users, Bitmap image,
                         GroupVisibility visibility, boolean validated) {
            super(name, id, users, image, visibility, validated);
        }
    }
}
