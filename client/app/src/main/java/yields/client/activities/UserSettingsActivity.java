package yields.client.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import yields.client.R;

public class UserSettingsActivity extends AppCompatActivity {

    /**
     * Method automatically called on the creation of the activity
     * @param savedInstanceState the previous instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
    }
}
