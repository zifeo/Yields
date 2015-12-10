package yields.client.activities;

import android.content.Intent;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import yields.client.R;
import yields.client.generalhelpers.MockModel;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class GoogleLoginActivityTests extends ActivityInstrumentationTestCase2<GoogleLoginActivity> {
    public GoogleLoginActivityTests() {
        super(GoogleLoginActivity.class);
    }

    /**
     * Set up for the tests.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    /**
     * TODO: Discuss usefulness of this test... (not working if too many requests)
     * Test that simply launches the activity.
     */
    public void testLaunch(){
        //getActivity();
    }
}
