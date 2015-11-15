package yields.client.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import yields.client.R;
import yields.client.gui.GraphicTransforms;
import yields.client.node.Group;
import yields.client.yieldsapplication.YieldsApplication;

public class GroupInfoActivity extends AppCompatActivity {
    private Group mGroup;

    private static final int MAX_TAGS = 10;

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
                "The group in YieldsApplication cannot be null when this activity is created");

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


    }
}
