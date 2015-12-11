package yields.client.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import yields.client.R;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the information about a user are displayed
 */
public class UserInfoActivity extends AppCompatActivity {

    private User mUser;

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Information");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Node node = Objects.requireNonNull(YieldsApplication.getUserSearched(),
                "getUserSearched() in YieldsApplication cannot be null when UserInfoActivity is created");

        ImageView imageView = (ImageView) findViewById(R.id.imageViewUser);
        int size = getResources().getInteger(R.integer.largeGroupImageDiameter);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(node.getImage(), size, size, false));

        TextView textViewName = (TextView) findViewById(R.id.textViewUserName);
        textViewName.setText(node.getName());

        if (node instanceof User) {
            TextView textViewEmail = (TextView) findViewById(R.id.textViewEmail);
            textViewEmail.setText(((User) node).getEmail());
            mUser = (User) node;
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
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Listener for the click on the user Image.
     * @param v The View.
     */
    public void showUserImage(View v){
        YieldsApplication.setShownImage(mUser.getImage());
        startActivity(new Intent(UserInfoActivity.this, ImageShowPopUp.class));
    }
}
