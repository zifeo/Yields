package yields.client.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import yields.client.R;
import yields.client.id.Id;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests for UserInfoActivity
 */
public class UserInfoActivityTests extends ActivityInstrumentationTestCase2<UserInfoActivity> {
    public UserInfoActivityTests() {
        super(UserInfoActivity.class);
    }

    /**
     * Set up for the tests
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        YieldsApplication.setUserSearched(MockFactory.generateFakeClientUser("Popol", new Id(1), "aa",
                YieldsApplication.getDefaultUserImage()));
    }

    /**
     * Test that the correct name is displayed
     */
    public void testCorrectName(){
        getActivity();

        onView(withId(R.id.textViewUserName)).check(matches(withText("Popol")));
    }
}
