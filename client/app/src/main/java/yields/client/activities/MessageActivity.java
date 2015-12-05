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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.SortedMap;

import yields.client.R;
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
import yields.client.servicerequest.NodeHistoryRequest;
import yields.client.servicerequest.NodeMessageRequest;
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
    private static Group mLastApplicationGroup;
    private static FragmentManager mFragmentManager;
    private static Fragment mCurrentFragment;

    private ListAdapterMessages mGroupMessageAdapter;
    private ListAdapterMessages mCommentAdapter;

    private static final int THUMBNAIL_PADDING = 6;
    private static ImageView mImageThumbnail;

    /**
     * Starts the activity by displaying the group info and showing the most
     * recent messages.
     *
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

        mSendButton = (ImageButton) findViewById(R.id.sendButton);

        // By default, we show the messages of the group.
        mType = ContentType.GROUP_MESSAGES;
        mFragmentManager = getFragmentManager();
        createGroupMessageFragment();

        NodeHistoryRequest historyRequest = new NodeHistoryRequest(mGroup, new Date());
        YieldsApplication.getBinder().sendRequest(historyRequest);

        mImageThumbnail = (ImageView) findViewById(R.id.imagethumbnail);
        mImageThumbnail.setPadding(0, 0, 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = YieldsApplication.getUser();
        mGroup = YieldsApplication.getGroup();
        setHeaderBar();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                retrieveGroupMessages();
            }
        });
    }

    /**
     * what to do when the activity is no more visible.
     */
    @Override
    public void onPause() {
        super.onPause();
        mCommentAdapter.clear();
        mGroupMessageAdapter.clear();
    }

    /**
     * What to do when activity shuts down.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        YieldsApplication.nullGroup();
    }

    /**
     * @return The id of the current group
     */
    public Id getGroupId() {
        return mGroup.getId();
    }

    /**
     * Method automatically called for the tool bar items
     *
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
    public void onSendMessage(View v) {
        String inputMessage = mInputField.getText().toString().trim();
        mInputField.setText("");
        if (!inputMessage.isEmpty() || mSendImage){
            Content content;
            if (mSendImage && mImage != null) {
                content = new ImageContent(mImage, inputMessage);
                mSendImage = false;
                mImage = null;
                mImageThumbnail.setImageBitmap(null);
                mImageThumbnail.setPadding(0, 0, 0, 0);
            } else {
                content = new TextContent(inputMessage);
            }

            Message message = new Message("message", new Id(0), mUser.getId(), content, new Date());
            if (mType == ContentType.GROUP_MESSAGES) {
                Log.d("MessageActivity", "Send group message");
                mGroupMessageAdapter.add(message);
                mGroupMessageAdapter.notifyDataSetChanged();
                ((GroupMessageFragment) mCurrentFragment).getMessageListView()
                        .smoothScrollToPosition(mGroupMessageAdapter.getCount() - 1);
                NodeMessageRequest request = new NodeMessageRequest(message, mGroup.getId(),
                        mGroup.getVisibility());
                YieldsApplication.getBinder().sendRequest(request);
            } else {
                mCommentAdapter.add(message);
                mCommentAdapter.notifyDataSetChanged();
                ((CommentFragment) mCurrentFragment).getCommentListView()
                        .smoothScrollToPosition(mCommentAdapter.getCount() - 1);
                NodeMessageRequest request = new NodeMessageRequest(message, mCommentMessage.getId(),
                        Group.GroupVisibility.PRIVATE);
                YieldsApplication.getBinder().sendRequest(request);
            }

            YieldsApplication.getGroup().addMessage(message);
        }
    }

    /**
     * Listener called when the user presses the picture icon.
     *
     * @param v The view which called this method
     */
    public void onClickAddImage(View v) {
        mSendImage = true;
        pickImageFromGallery();
        Log.d("MessageActivity", "Test null image");
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
                    Log.d("MessageActivity", "Update Thumbnail");
                    mImageThumbnail.setPadding(THUMBNAIL_PADDING, THUMBNAIL_PADDING,
                            THUMBNAIL_PADDING, THUMBNAIL_PADDING);
                    mImageThumbnail.setImageBitmap(mImage);
                    String message = "Image added to message";
                    YieldsApplication.showToast(getApplicationContext(), message);
                }
            } catch (IOException e) {
                Log.d("Message Activity", "Couldn't add image to the message");
            }
        }
    }

    /**
     * Method used to take care of clicks on the tool bar
     *
     * @param item The tool bar item clicked
     * @return true iff the click is not propagated
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("MessageActivity", "Back upper arrow pressed.");
                onBackPressed();
                return true;

            case R.id.actionSettingsGroup:
                Log.d("MessageActivity", "actionSettingsGroup.");
                Intent intent = new Intent(this, GroupSettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.iconConnect:
                YieldsApplication.getBinder().reconnect();
                return true;

            default:
                Log.d("MessageActivity", "default.");
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Notify the activity that the
     * data set has changed.
     */
    @Override
    public void notifyChange(Change change) {
        switch (change) {
            case MESSAGES_RECEIVE:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mType == ContentType.GROUP_MESSAGES) {
                            retrieveGroupMessages();
                        } else {
                            retrieveCommentMessages();
                        }
                    }
                });
                break;
            default:
                Log.d("Y:" + this.getClass().getName(), "useless notify change...");
        }

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
     *
     * @return If the current fragment is a groupMessageFragment then the
     * method returns the list view containing the messages (currently in
     * local memory) of the group.
     * If the current fragment is a commentFragment then the method
     * returns the list view containing the comments for the
     * message the user had clicked on.
     */
    public ListView getCurrentFragmentListView() {
        if (mType == ContentType.GROUP_MESSAGES) {
            Log.d("MessageActivity", "NODE_MESSAGE ListView");
            return ((GroupMessageFragment) mCurrentFragment)
                    .getMessageListView();
        } else {
            Log.d("MessageActivity", "MESSAGE_COMMENT ListView");
            return ((CommentFragment) mCurrentFragment)
                    .getCommentListView();
        }
    }

    /**
     * Getter for the fragment currently displayed.
     *
     * @return The current fragment.
     */
    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    /**
     * Getter for the type of fragment.
     *
     * @return The type of the fragment currently displayed.
     */
    public ContentType getType() {
        return mType;
    }

    /**
     * Allows tests to send image message instead of text message and thus being able to comment
     * them.
     */
    public void simulateImageMessage() {
        mImage = YieldsApplication.getDefaultGroupImage();
        mSendImage = true;
    }

    /**
     * Cancel an image in a message when clicking on the thumbnail.
     * @param v The view clicked on.
     */
    public void cancelImageSending(View v){
        String message = "Image removed from message";
        YieldsApplication.showToast(YieldsApplication.getApplicationContext(), message);
        mImageThumbnail.setPadding(0, 0, 0, 0);
        mSendImage = false;
        mImageThumbnail.setImageBitmap(null);
        mImage = null;
    }

    /**
     * Creates a comment fragment and put it in the fragment container of the
     * MessageActivity (id fragmentPlaceHolder).
     */
    private void createCommentFragment() {
        Log.d("MessageActivity", "createCommentFragment");
        mInputField.setText("");
        FragmentTransaction fragmentTransaction = mFragmentManager.
                beginTransaction();
        assert (mType == ContentType.MESSAGE_COMMENTS);
        mActionBar.setTitle("Message from " + YieldsApplication.getUser(mCommentMessage.getSender())
                .getName());
        mCurrentFragment = new CommentFragment();
        mCommentAdapter.clear();
        ((CommentFragment) mCurrentFragment).setAdapter(mCommentAdapter);
        ((CommentFragment) mCurrentFragment).setMessage(mCommentMessage);
        ((CommentFragment) mCurrentFragment).setCommentViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CommentFragment", "CommentView clicked.");
                if (mCommentMessage.getContent().isCommentable()) {
                    YieldsApplication.setShownImage(((ImageContent) mCommentMessage.getContent()).getImage());
                    startActivity(new Intent(MessageActivity.this, ImageShowPopUp.class));
                }
            }
        });
        fragmentTransaction.replace(R.id.fragmentPlaceHolder, mCurrentFragment);
        fragmentTransaction.commit();
        loadComments();
    }

    /**
     * Make request to load comments for a message and sends it to the server using the
     * ServiceBinder.
     */
    private void loadComments() {
        Log.d("MessageActivity", "loadComments");
        NodeHistoryRequest request = new NodeHistoryRequest(mGroup, new Date());
        YieldsApplication.getBinder().sendRequest(request);
    }

    /**
     * Creates a group message fragment and put it in the fragment container of
     * the MessageActivity (id fragmentPlaceHolder).
     */
    private void createGroupMessageFragment() {
        Log.d("MessageActivity", "createGroupMessageFragment");
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
                        Message message = mGroupMessageAdapter.getItem(position);
                        // Only non text messages can be commented.
                        if (message.getContent().isCommentable()) {
                            // First keep a reference to the message that has been clicked on.
                            mCommentMessage = message;
                            // We save the reference of the last group in the YieldsApplication class.
                            mLastApplicationGroup = mGroup;
                            // Then we update the group currently displayed as it is the commented
                            // message
                            mGroup = Group.createGroupForMessageComment(mCommentMessage, mGroup);
                            YieldsApplication.setGroup(mGroup);
                            mType = ContentType.MESSAGE_COMMENTS;
                            createCommentFragment();
                        }
                    }
                });
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
        if (mType == ContentType.GROUP_MESSAGES) {
            Log.d("MessageActivity", "Quit activity");
            super.onBackPressed();
        } else {
            Log.d("MessageActivity", "Back to group message fragment");
            mType = ContentType.GROUP_MESSAGES;
            // We need to go back to the last reference of mGroup.
            mGroup = mLastApplicationGroup;
            YieldsApplication.setGroup(mGroup);
            createGroupMessageFragment();
        }
    }

    /**
     * Retrieve message from the server and puts them in the group message adapter.
     */
    private void retrieveGroupMessages() {
        Log.d("MessageActivity", "retrieveGroupMessages");
        SortedMap<Date, Message> messagesTree = mGroup.getLastMessages();

        if(mGroupMessageAdapter.getCount() < messagesTree.size()) {
            Log.d("Y:" + this.getClass().getName(), "retrieveGroupMessages");
            mGroupMessageAdapter.clear();

            for (Message message : messagesTree.values()) {
                mGroupMessageAdapter.add(message);
            }

            Log.d("Y:" + this.getClass().getName(), "retrieveGroupMessages");
        }

        mGroupMessageAdapter.notifyDataSetChanged();
        ((GroupMessageFragment) mCurrentFragment).getMessageListView()
                .smoothScrollToPosition(mGroupMessageAdapter.getCount() - 1);
    }

    /**
     * Retrieve comments for a message an puts them in the comments adapter.
     */
    private void retrieveCommentMessages() {
        SortedMap<Date, Message> messagesTree = mGroup.getLastMessages();

        if(mCommentAdapter.getCount() < messagesTree.size()) {
            Log.d("Y:" + this.getClass().getName(), "retrieveCommentMessages");
            mCommentAdapter.clear();

            for (Message message : messagesTree.values()) {
                mCommentAdapter.add(message);
            }
        }

        mCommentAdapter.notifyDataSetChanged();
        ((CommentFragment) mCurrentFragment).getCommentListView()
                .smoothScrollToPosition(mCommentAdapter.getCount() - 1);
    }

    /**
     * Starts the activity which allows the user to pick which image from his
     * gallery he wants to send.
     */
    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    /**
     * Sets the correct information on the header.
     */
    private void setHeaderBar() {
        if (mUser == null || mGroup == null) {
            String message = "Couldn't get group information.";
            YieldsApplication.showToast(getApplicationContext(), message);
            mActionBar.setTitle("Unknown group");
        } else {
            mActionBar.setTitle(mGroup.getName());
        }
    }
}
