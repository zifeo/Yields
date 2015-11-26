package yields.client.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.exceptions.IllegalIntentExtraException;
import yields.client.exceptions.MissingIntentExtraException;
import yields.client.node.Group;
import yields.client.servicerequest.GroupAddRequest;
import yields.client.servicerequest.GroupRemoveRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the main information of a group are displayed :
 * name, image, tags.
 */
public class GroupInfoActivity extends NotifiableActivity {
    private Group mGroup;

    private static final int MAX_TAGS = 10;

    private SearchGroupActivity.Mode mMode;

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Information");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Intent intent = getIntent();
        if (!intent.hasExtra(SearchGroupActivity.MODE_KEY)) {
            throw new MissingIntentExtraException(
                    "Mode extra is missing from intent in GroupInfoActivity");
        }

        int indexMode = intent.getIntExtra(SearchGroupActivity.MODE_KEY, 0);

        if (indexMode < 0 || indexMode >= SearchGroupActivity.Mode.values().length){
            throw new IllegalIntentExtraException(
                    "Mode extra must be between 0 and "
                            + (SearchGroupActivity.Mode.values().length - 1) +  " in GroupInfoActivity");
        }

        mMode = SearchGroupActivity.Mode.values()[indexMode];

        mGroup = Objects.requireNonNull(YieldsApplication.getGroup(),
                "The group in YieldsApplication cannot be null when GroupInfoActivity is created");

        ImageView imageView = (ImageView) findViewById(R.id.imageViewGroup);
        int size = getResources().getInteger(R.integer.largeGroupImageDiameter);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(mGroup.getImage(), size, size, false));

        TextView textViewName = (TextView) findViewById(R.id.textViewGroupName);
        textViewName.setText(mGroup.getName());

        List<Group.Tag> tags = mGroup.getTagList();

        TextView textViewTags = (TextView) findViewById(R.id.textViewTags);

        if (tags.size() == 0){
            textViewTags.setText(getString(R.string.noTags));
        }
        else if (tags.size() == 1){
            String text = "Tag : " + tags.get(0).getText();
            textViewTags.setText(text);
        }
        else {
            StringBuilder builder = new StringBuilder("Tags : ");
            for (int i = 0; i < MAX_TAGS && i < tags.size(); i++){
                if (i != 0){
                    builder.append(", ");
                }
                builder.append(tags.get(i).getText());
            }
            textViewTags.setText(builder.toString());
        }

        Button usersButton = (Button) findViewById(R.id.buttonUsers);

        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupInfoActivity.this, UserListActivity.class);
                String title = "Users of " + mGroup.getName();
                intent.putExtra(UserListActivity.TITLE_KEY, title);
                startActivity(intent);
            }
        });

        checkButtons();
    }

    /** Method used to take care of clicks on the tool bar
     *
     * @param item The tool bar item clicked
     * @return true iff the click is not propagated
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Notify the activity that a response has been received
     */
    @Override
    public void notifyChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkButtons();

                if (mGroup.containsUser(YieldsApplication.getUser())){
                    YieldsApplication.showToast(getApplicationContext(), "Group joined !");
                }
                else {
                    YieldsApplication.showToast(getApplicationContext(), "Group left !");
                }
            }
        });
    }

    /**
     * Method called when the server is connected
     */
    @Override
    public void notifyOnServerConnected() {

    }

    /**
     * Method called when the server is disconnected
     */
    @Override
    public void notifyOnServerDisconnected() {

    }

    /**
     * Check if the user is in the group and set the appropriate states to the buttons
     */
    private void checkButtons(){
        if (mMode == SearchGroupActivity.Mode.SEARCH){
            YieldsApplication.setUserList(mGroup.getUsers());

            final Button joinButton = (Button) findViewById(R.id.buttonJoinGroup);

            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ServiceRequest request = new GroupAddRequest(YieldsApplication.getUser(),
                            mGroup.getId(), YieldsApplication.getUser());
                    YieldsApplication.getBinder().sendRequest(request);

                    joinButton.setEnabled(false);
                }
            });

            final Button leaveButton = (Button) findViewById(R.id.buttonLeaveGroup);

            leaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ServiceRequest request = new GroupRemoveRequest(YieldsApplication.getUser(),
                            mGroup.getId(), YieldsApplication.getUser().getId());
                    YieldsApplication.getBinder().sendRequest(request);

                    leaveButton.setEnabled(false);
                }
            });

            if (mGroup.containsUser(YieldsApplication.getUser())){
                joinButton.setVisibility(View.GONE);
                leaveButton.setVisibility(View.VISIBLE);
            }
            else {
                joinButton.setVisibility(View.VISIBLE);
                leaveButton.setVisibility(View.GONE);
            }
        }
        else {
            final Button addButton = (Button) findViewById(R.id.buttonAddGroup);
            addButton.setVisibility(View.VISIBLE);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YieldsApplication.setGroupAdded(mGroup);
                    YieldsApplication.setGroupAddedValid(true);

                    Intent intent = new Intent(GroupInfoActivity.this, CreateGroupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    startActivity(intent);
                }
            });
        }
    }
}
