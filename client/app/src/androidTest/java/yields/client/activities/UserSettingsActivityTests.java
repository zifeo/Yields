package yields.client.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;

import yields.client.R;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.id.Id;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.onView;
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

/**
 * Tests for UserSettingsActivity
 */
public class UserSettingsActivityTests extends ActivityInstrumentationTestCase2<UserSettingsActivity> {
    public UserSettingsActivityTests() {
        super(UserSettingsActivity.class);

        ServiceTestConnection.connectActivityToService();
    }

    /**
     * Set up for the tests.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        ClientUser user = MockFactory.generateFakeClientUser("User", new Id(123), "a@b.c",
                YieldsApplication.getDefaultUserImage());
        YieldsApplication.setUser(user);
    }

    @Override
    public void tearDown(){
        YieldsApplication.cancelToast();
    }

    /**
     * Test that tries to change the username
     */
    public void testChangeUsername() {
        getActivity();
        onView(withText(R.string.changeUserName)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("Arnaud2"), closeSoftKeyboard());

        onView(withText("Ok")).perform(click());

        onView(withText("Username changed to \"Arnaud2\" !")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test that tries to change the username to a short one
     */
    public void testAddUsernameTooShort() {
        getActivity();
        onView(withText(R.string.changeUserName)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("a"), closeSoftKeyboard());

        onView(withText("Ok")).perform(click());
        onView(withText("Cancel")).perform(click());

        onView(withText("The username is too short")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}
