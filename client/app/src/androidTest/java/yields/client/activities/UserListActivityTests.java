package yields.client.activities;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;

import yields.client.R;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class UserListActivityTests extends ActivityInstrumentationTestCase2<UserListActivity> {
    public UserListActivityTests() {
        super(UserListActivity.class);

        ServiceTestConnection.connectActivityToService();
    }

    /**
     * Set up for the tests.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        Intent intent = new Intent();
        intent.putExtra(UserListActivity.TITLE_KEY, "Hello");
        intent.putExtra(UserListActivity.SHOW_ADD_ENTOURAGE_KEY, true);
        setActivityIntent(intent);

        YieldsApplication.setUserList(new ArrayList<User>());
    }

    @Override
    public void tearDown() {
        YieldsApplication.cancelToast();
    }

    /**
     * Test that the correct name is displayed
     */
    public void testAddUser(){
        getActivity();
        onView(withText("Add a new contact")).perform(click());

        onView(withId(R.id.editTextEmail)).check(matches(isDisplayed()));
    }
}
