package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.gui.GraphicTransforms;
import yields.client.node.Group;
import yields.client.servicerequest.GroupAddRequest;
import yields.client.servicerequest.GroupRemoveRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the main information of a group are displayed :
 * name, image, tags.
 */
public class GroupInfoActivity extends AppCompatActivity implements NotifiableActivity{
    private Group mGroup;

    private static final int MAX_TAGS = 10;

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

        mGroup = Objects.requireNonNull(YieldsApplication.getGroup(),
                "The group in YieldsApplication cannot be null when GroupInfoActivity is created");

        ImageView imageView = (ImageView) findViewById(R.id.imageViewGroup);
        imageView.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(mGroup.getImage(),
                getResources().getInteger(R.integer.groupImageDiameter)));

        TextView textViewName = (TextView) findViewById(R.id.textViewGroupName);
        textViewName.setText(mGroup.getName());

        List<Group.Tag> tags = mGroup.getTagList();

        TextView textViewTags = (TextView) findViewById(R.id.textViewTags);

        if (tags.size() == 0){
            textViewTags.setText(getString(R.string.noTags));
        }
        else if (tags.size() == 1){
            textViewTags.setText("Tag : " + tags.get(0).getText());
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
     * Check if the user is in the group and set the appropriate states to the buttons
     */
    private void checkButtons(){
        YieldsApplication.setUserList(mGroup.getUsers());

        final Button joinButton = (Button) findViewById(R.id.buttonJoinGroup);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceRequest request = new GroupAddRequest(YieldsApplication.getUser(),
                        mGroup, YieldsApplication.getUser());
                YieldsApplication.getBinder().sendRequest(request);

                joinButton.setEnabled(false);
            }
        });

        final Button leaveButton = (Button) findViewById(R.id.buttonLeaveGroup);

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceRequest request = new GroupRemoveRequest(YieldsApplication.getUser(),
                        mGroup, YieldsApplication.getUser());
                YieldsApplication.getBinder().sendRequest(request);

                leaveButton.setEnabled(false);
            }
        });

        if (mGroup.containsUser(YieldsApplication.getUser())){
            joinButton.setVisibility(View.GONE);
        }
        else {
            leaveButton.setVisibility(View.GONE);
        }
    }
}
