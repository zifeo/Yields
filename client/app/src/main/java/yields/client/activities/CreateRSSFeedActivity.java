package yields.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

import yields.client.R;
import yields.client.exceptions.MissingIntentExtraException;
import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.servicerequest.RSSCreateRequest;
import yields.client.servicerequest.ServiceRequest;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Activity where the user can type in the url of the rss feed
 * and some keywords to filter the future feeds.
 */
public class CreateRSSFeedActivity extends NotifiableActivity {

    private EditText mEditTextUrl;
    private EditText mEditTextKeywords;

    private String mGroupName;

    /**
     * Method automatically called on the creation of the activity
     *
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rssfeed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mEditTextUrl = (EditText) findViewById(R.id.editTextUrl);
        mEditTextKeywords = (EditText) findViewById(R.id.editTextKeyWords);

        Intent intent = getIntent();

        if (!intent.hasExtra(CreateGroupSelectNameActivity.GROUP_NAME_KEY)) {
            throw new MissingIntentExtraException(
                    "Group name extra is missing from intent in CreateRSSFeedActivity");
        }

        mGroupName = intent.getStringExtra(CreateGroupSelectNameActivity.GROUP_NAME_KEY);
    }

    /**
     * Method automatically called for the tool bar items
     *
     * @param menu The tool bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_create_rssfeed, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method called when the user clicks on 'Done'
     *
     * @param item The tool bar item clicked
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String url = mEditTextUrl.getText().toString();

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            String message = getString(R.string.messageUrlHttp);
            YieldsApplication.showToast(getApplicationContext(), message);
        } else {
            Group rss = new Group(mGroupName, new Id(0), new ArrayList<Id>());
            YieldsApplication.getUser().addNode(rss);

            ServiceRequest rssCreaterequest = new RSSCreateRequest(
                    YieldsApplication.getUser().getId(), url, rss,
                    mEditTextKeywords.getText().toString());
            YieldsApplication.getBinder().sendRequest(rssCreaterequest);
        }

        return true;
    }

    @Override
    public void notifyChange(Change change) {
        switch (change) {
            case RSS_CREATE:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message = getString(R.string.messageRssCreated);
                        YieldsApplication.showToast(getApplicationContext(), message);

                        Intent createGroupIntent = new Intent(CreateRSSFeedActivity.this, GroupActivity.class);
                        createGroupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        startActivity(createGroupIntent);
                    }
                });
                break;
            case RSS_FAIL:
                YieldsApplication.showToast(getApplicationContext(), "The RSS cannot be found");
                break;
            default:
                Log.d("Y:" + this.getClass().getName(), "useless notify change...");
        }
    }

    @Override
    public void notifyOnServerConnected() {

    }

    @Override
    public void notifyOnServerDisconnected() {

    }
}
