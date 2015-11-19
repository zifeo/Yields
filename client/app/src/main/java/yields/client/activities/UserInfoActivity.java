package yields.client.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.gui.GraphicTransforms;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the information about a user are displayed
 */
public class UserInfoActivity extends AppCompatActivity {

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
        getSupportActionBar().setTitle("Group Information");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        User user = Objects.requireNonNull(YieldsApplication.getUserSearched(),
                "getUserSearched() in YieldsApplication cannot be null when this activity is created");

        ImageView imageView = (ImageView) findViewById(R.id.imageViewUser);
        imageView.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(user.getImg(),
                getResources().getInteger(R.integer.groupImageDiameter)));

        TextView textViewName = (TextView) findViewById(R.id.textViewUserName);
        textViewName.setText(user.getName());
    }
}
