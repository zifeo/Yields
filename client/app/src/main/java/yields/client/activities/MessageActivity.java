package yields.client.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.SortedMap;

import yields.client.R;
import yields.client.exceptions.MessageActivityException;
import yields.client.fragments.CommentFragment;
import yields.client.fragments.GroupMessageFragment;
import yields.client.id.Id;
import yields.client.listadapter.ListAdapterMessages;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.messages.UrlContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.service.YieldService;
import yields.client.service.YieldServiceBinder;
import yields.client.servicerequest.GroupMessageRequest;
import yields.client.servicerequest.MediaMessageRequest;
import yields.client.servicerequest.NodeHistoryRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity used to display messages for a group, or comments of a Message.
 */
public class MessageActivity extends NotifiableActivity {


    public enum ContentType {GROUP_MESSAGES, MESSAGE_COMMENTS}

    private static ClientUser mUser;
    private Group mGroup;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap mImagePickedFromGallery;
    private boolean mSendImage;
    private static EditText mInputField;
    private static ActionBar mActionBar;
    private static TextView mTextTitle;
    private Menu mMenu;

    private ServiceConnection mConnection;

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
        mActionBar.setTitle(null);

        mTextTitle = (TextView) findViewById(R.id.toolbarTitle);

        mType = ContentType.GROUP_MESSAGES;

        mTextTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType == ContentType.GROUP_MESSAGES) {
                    YieldsApplication.setInfoGroup(mGroup);
                    Intent intent = new Intent(MessageActivity.this, GroupInfoActivity.class);
                    intent.putExtra(SearchGroupActivity.MODE_KEY, 0);
                    startActivity(intent);
                }
            }
        });

        mUser = YieldsApplication.getUser();


        mImagePickedFromGallery = null;
        mSendImage = false;

        mGroupMessageAdapter = new ListAdapterMessages(YieldsApplication
                .getApplicationContext(), R.layout.messagelayoutsender, new ArrayList<Message>());
        mCommentAdapter = new ListAdapterMessages((YieldsApplication
                .getApplicationContext()), R.layout.messagelayoutsender, new ArrayList<Message>());

        mInputField = (EditText) findViewById(R.id.inputMessageField);

        mFragmentManager = getFragmentManager();

        if (this.getIntent().getBooleanExtra(YieldService.NOTIFICATION, false)) {
            Id groupId = new Id(this.getIntent().getLongExtra(YieldService.GROUP_RECEIVING, 0));
            if (groupId.getId() != 0) {
                mGroup = mUser.getGroup(groupId);
                if (mGroup == null) {
                    mGroup = mUser.getCommentGroup(groupId);
                    if (mGroup == null) {
                        YieldsApplication.setGroup(mGroup);
                        mType = ContentType.MESSAGE_COMMENTS;
                        createCommentFragment();
                    } else {
                        throw new IllegalStateException("there is no group for id : " + groupId.getId());
                    }
                } else {
                    YieldsApplication.setGroup(mGroup);
                    YieldsApplication.setInfoGroup(mGroup);
                    mType = ContentType.GROUP_MESSAGES;
                    createGroupMessageFragment();
                }
            }
        } else {
            mGroup = YieldsApplication.getGroup();
            mType = ContentType.GROUP_MESSAGES;
            createGroupMessageFragment();
        }

        YieldsApplication.getBinder().cancelNotificationFromId(mGroup.getId());

        if (YieldsApplication.getBinder() != null) {
            NodeHistoryRequest historyRequest = new NodeHistoryRequest(mGroup.getId(), new Date());
            YieldsApplication.getBinder().sendRequest(historyRequest);
        } else {
            mConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    YieldsApplication.setGroup(mGroup);
                    YieldsApplication.setBinder((YieldServiceBinder) service);
                    YieldsApplication.getBinder().attachActivity(MessageActivity.this);
                    YieldsApplication.getBinder().connectionStatus();

                    NodeHistoryRequest historyRequest = new NodeHistoryRequest(mGroup.getId(), new Date());
                    YieldsApplication.getBinder().sendRequest(historyRequest);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mConnection = null;
                }
            };

            Intent serviceBindingIntent = new Intent(this, YieldService.class)
                    .putExtra("bindGroupActivity", true);

            bindService(serviceBindingIntent, mConnection, Context.BIND_AUTO_CREATE);
        }

        mFragmentManager = getFragmentManager();
        createGroupMessageFragment();

        mImageThumbnail = (ImageView) findViewById(R.id.imagethumbnail);
        mImageThumbnail.setPadding(0, 0, 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = YieldsApplication.getUser();
        YieldsApplication.setGroup(mGroup);
        setHeaderBar();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (MessageActivity.this.getType()) {
                    case MESSAGE_COMMENTS:
                        retrieveCommentMessages();
                        break;
                    case GROUP_MESSAGES:
                        retrieveGroupMessages();
                        break;
                }
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
        YieldsApplication.nullGroup();
    }

    /**
     * What to do when activity shuts down.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnection != null) {
            unbindService(mConnection);
        }
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
        if (YieldsApplication.getBinder() != null) {
            YieldsApplication.getBinder().connectionStatus();
        }
        return res;
    }

    /**
     * Listener called when the user sends a message to the group.
     */
    public void onSendMessage(View v) {
        String inputMessage = mInputField.getText().toString().trim();
        mInputField.setText("");

        if (!inputMessage.isEmpty() || mSendImage) {
            Content content;

            if (mSendImage && mImagePickedFromGallery != null) {
                content = new ImageContent(mImagePickedFromGallery, inputMessage);
                Log.d("Y:" + this.getClass().toString(), "Created image content");
                mSendImage = false;
                mImagePickedFromGallery = null;
                mImageThumbnail.setImageBitmap(null);
                mImageThumbnail.setPadding(0, 0, 0, 0);
            } else if (UrlContent.containsUrl(inputMessage)) {
                content = new UrlContent(inputMessage);
                Log.d("Y:" + this.getClass().toString(), "Created url content");
            } else {
                content = new TextContent(inputMessage);
                Log.d("Y:" + this.getClass().toString(), "Create text content");
            }

            Message message = new Message(new Id(-1), mUser.getId(), content, new Date());
            if (mType == ContentType.GROUP_MESSAGES) {
                Log.d("Y:" + this.getClass().toString(), "Send group message to " + mGroup.getId().getId().toString());
                mGroup.addMessage(message);
                mGroupMessageAdapter.add(message);
                mGroupMessageAdapter.notifyDataSetChanged();
                ((GroupMessageFragment) mCurrentFragment).getMessageListView()
                        .smoothScrollToPosition(mGroupMessageAdapter.getCount() - 1);

                GroupMessageRequest request = new GroupMessageRequest(message, mGroup.getId(), mGroup.getType());
                YieldsApplication.getBinder().sendRequest(request);
            } else {
                Log.d("Y:" + this.getClass().toString(), "Send media message to " + mGroup.getId().getId().toString());
                mGroup.addMessage(message);
                mCommentAdapter.add(message);
                mCommentAdapter.notifyDataSetChanged();

                ((CommentFragment) mCurrentFragment).getCommentListView()
                        .smoothScrollToPosition(mCommentAdapter.getCount() - 1);
                MediaMessageRequest request = new MediaMessageRequest(message,
                        mCommentMessage.getCommentGroupId());
                YieldsApplication.getBinder().sendRequest(request);
            }
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
                mImagePickedFromGallery = MediaStore.Images.Media.getBitmap(getContentResolver(),
                        uri);
                if (mImagePickedFromGallery != null) {
                    Log.d("MessageActivity", "Update Thumbnail");
                    mImageThumbnail.setPadding(THUMBNAIL_PADDING, THUMBNAIL_PADDING,
                            THUMBNAIL_PADDING, THUMBNAIL_PADDING);
                    mImageThumbnail.setImageBitmap(mImagePickedFromGallery);
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
                Log.d("Y:" + this.getClass().getName(), "Useless notify change...");
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
        mImagePickedFromGallery = YieldsApplication.getDefaultGroupImage();
        mSendImage = true;
    }

    /**
     * Cancel an image in a message when clicking on the thumbnail.
     *
     * @param v The view clicked on.
     */
    public void cancelImageSending(View v) {
        String message = "Image removed from message";
        YieldsApplication.showToast(YieldsApplication.getApplicationContext(), message);
        mImageThumbnail.setPadding(0, 0, 0, 0);
        mSendImage = false;
        mImageThumbnail.setImageBitmap(null);
        mImagePickedFromGallery = null;
    }

    /**
     * Creates a comment fragment and put it in the fragment container of the
     * MessageActivity (id fragmentPlaceHolder).
     */
    private void createCommentFragment() {
        Log.d("MessageActivity", "createCommentFragment");
        mInputField.setText("");
        YieldsApplication.getBinder().attachActivity(this);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        assert (mType == ContentType.MESSAGE_COMMENTS);
        mTextTitle.setText("Message from " + YieldsApplication.getNodeFromId(mCommentMessage.getSender())
                .getName());
        mCurrentFragment = new CommentFragment();
        retrieveCommentMessages();
        ((CommentFragment) mCurrentFragment).setAdapter(mCommentAdapter);
        ((CommentFragment) mCurrentFragment).setMessage(mCommentMessage);
        ((CommentFragment) mCurrentFragment).setCommentViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CommentFragment", "CommentView clicked.");
                if (mCommentMessage.getContent().isCommentable()) {
                    switch (mCommentMessage.getContent().getType()) {
                        case IMAGE:
                            YieldsApplication.setShownImage(((ImageContent) mCommentMessage.getContent()).
                                    getImage());
                            startActivity(new Intent(MessageActivity.this, ImageShowPopUp.class));
                            break;

                        case URL:
                            String url = ((UrlContent) mCommentMessage.getContent()).getUrl();
                            Log.d("MessageActivity", "Open URL in browser : " + url);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                            break;

                        default:
                            throw new MessageActivityException("Error, unsupported operation for this type" +
                                    " of content");
                    }
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
        YieldsApplication.getBinder().attachActivity(this);
        NodeHistoryRequest request = new NodeHistoryRequest(mCommentMessage.getCommentGroupId(),
                new Date());
        YieldsApplication.getBinder().sendRequest(request);
    }

    /**
     * Creates a group message fragment and put it in the fragment container ofg
     * the MessageActivity (id fragmentPlaceHolder).
     */
    private void createGroupMessageFragment() {
        mInputField.setText("");
        FragmentTransaction fragmentTransaction = mFragmentManager.
                beginTransaction();
        assert (mType == ContentType.GROUP_MESSAGES);
        mTextTitle.setText(mGroup.getName());
        mCurrentFragment = new GroupMessageFragment();
        ((GroupMessageFragment) mCurrentFragment).setAdapter(mGroupMessageAdapter);
        retrieveGroupMessages();
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
                            Group commentGroup = YieldsApplication.getUser()
                                    .getCommentGroup(mCommentMessage.getCommentGroupId());

                            if (commentGroup == null) {
                                commentGroup = Group.createGroupForMessageComment(mCommentMessage, mGroup);
                                YieldsApplication.getUser().addCommentGroup(commentGroup);
                            }
                            mGroup = commentGroup;
                            YieldsApplication.setGroup(mGroup);
                            mType = ContentType.MESSAGE_COMMENTS;
                            createCommentFragment();
                        }
                    }
                });
        fragmentTransaction.replace(R.id.fragmentPlaceHolder, mCurrentFragment);
        fragmentTransaction.commit();
        Log.d("Y:" + this.getClass().toString(), "Created group message fragment");
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
            Log.d("Y:" + this.getClass().toString(), "Back button pressed");
            super.onBackPressed();
        } else {
            Log.d("Y:" + this.getClass().toString(), "Back button pressed, going back to Group Messages");
            mType = ContentType.GROUP_MESSAGES;
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

        if (mGroupMessageAdapter.getCount() < messagesTree.size()) {
            mGroupMessageAdapter.clear();
            Log.d("Y:" + this.getClass().getName(), "Cleared message adapter");

            for (Message message : messagesTree.values()) {
                mGroupMessageAdapter.add(message);
            }
            Log.d("Y:" + this.getClass().getName(), "Added most recent messages in adapter");
        }

        ListView listView = ((GroupMessageFragment) mCurrentFragment).getMessageListView();

        if (mGroupMessageAdapter.getCount() - listView.getSelectedItemPosition() > 3) {
            listView.setSelection(mGroupMessageAdapter.getCount() - 1);
        } else {
            listView.smoothScrollToPosition(mGroupMessageAdapter.getCount() - 1);
        }

        mGroupMessageAdapter.notifyDataSetChanged();
    }

    /**
     * Retrieve comments for a message an puts them in the comments adapter.
     */
    private void retrieveCommentMessages() {
        SortedMap<Date, Message> messagesTree = mGroup.getLastMessages();

        mCommentAdapter.clear();

        for (Message message : messagesTree.values()) {
            mCommentAdapter.add(message);
        }

        ListView listView = ((CommentFragment) mCurrentFragment).getCommentListView();

        if (mCommentAdapter.getCount() - listView.getSelectedItemPosition() > 3) {
            listView.setSelection(mCommentAdapter.getCount() - 1);
        } else {
            listView.smoothScrollToPosition(mCommentAdapter.getCount() - 1);
        }

        mCommentAdapter.notifyDataSetChanged();
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
            mTextTitle.setText("Unknown group");
        } else {
            mTextTitle.setText(mGroup.getName());
        }
    }
}
