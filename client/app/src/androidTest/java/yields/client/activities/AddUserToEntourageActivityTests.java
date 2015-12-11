package yields.client.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import yields.client.R;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.id.Id;
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
 * Tests for AddUserToEntourageActivity
 */
public class AddUserToEntourageActivityTests extends ActivityInstrumentationTestCase2<AddUserToEntourageActivity> {
    public AddUserToEntourageActivityTests() {
        super(AddUserToEntourageActivity.class);

        ServiceTestConnection.connectActivityToService();
    }

    /**
     * Set up for the tests
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        YieldsApplication.setUser(MockFactory.generateFakeClientUser("Arnaud", new Id(1), "aa",
                YieldsApplication.getDefaultUserImage()));
    }

    /**
     * Test that the incorrect email is not sent
     */
    public void testIncorrectEmail(){
        getActivity();

        onView(withId(R.id.editTextEmail)).perform(typeText("a"), closeSoftKeyboard());
        onView(withId(R.id.actionDoneEnterEmail)).perform(click());

        onView(withText(R.string.messageWrongEmail)).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test that the correct email is sent.
     */
    public void testCorrectAdd(){
        getActivity();

        onView(withId(R.id.editTextEmail)).perform(typeText("a@a.c"), closeSoftKeyboard());
        getActivity().notifyChange(NotifiableActivity.Change.ADD_ENTOURAGE);
    }

    /**
     * Test that the correct email is sent, but
     * it simulates that the user is not found.
     */
    public void testCorrectAddNotFound(){
        getActivity();

        onView(withId(R.id.editTextEmail)).perform(typeText("a@a.c"), closeSoftKeyboard());
        getActivity().notifyChange(NotifiableActivity.Change.NOT_EXIST);

        onView(withText(R.string.messageNoUser)).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}
