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

/**
 * Tests for GroupSettingsActivity
 */
public class GroupSettingsActivityTests extends ActivityInstrumentationTestCase2<GroupSettingsActivity> {
    public GroupSettingsActivityTests() {
        super(GroupSettingsActivity.class);

        ServiceTestConnection.connectActivityToService();
    }

    /**
     * Set up for the tests.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        ClientUser user = MockFactory.generateFakeClientUser("User", new Id(123), "a@b.c", YieldsApplication.getDefaultUserImage());
        user.addUserToEntourage(MockFactory.generateFakeClientUser("Friend", new Id(125), "a@b.d", YieldsApplication.getDefaultUserImage()));

        YieldsApplication.setUser(user);

        Group g = new Group("Group", new Id(124), new ArrayList<Id>());
        g.setVisibility(Group.GroupVisibility.PUBLIC);
        YieldsApplication.setGroup(g);
    }

    @Override
    public void tearDown(){
        YieldsApplication.cancelToast();
    }

    /**
     * Test that tries to change the name of the group
     */
    public void testChangeName() {
        getActivity();
        onView(withText(R.string.changeGroupName)).perform(click());
        onView(withId(R.id.editText)).perform(typeText(" SWENG"), closeSoftKeyboard());

        onView(withText("Ok")).perform(click());

        onView(withText("Group name changed to \"Group SWENG\" !")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test that tries to change the group's name to a short one
     */
    public void testNameTooShort() {
        getActivity();
        onView(withText(R.string.changeGroupName)).perform(click());
        onView(withId(R.id.editText)).perform(clearText(), typeText("a"), closeSoftKeyboard());

        onView(withText("Ok")).perform(click());
        onView(withText("Cancel")).perform(click());

        onView(withText("The new name is too short")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test that tries to add a short tag
     */
    public void testAddTagTooShort() {
        getActivity();
        onView(withText(R.string.addTag)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("a"), closeSoftKeyboard());

        onView(withText("Ok")).perform(click());
        onView(withText("Cancel")).perform(click());

        onView(withText("The tag is too short")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test that tries to add a long tag
     */
    public void testAddTagTooLong() {
        getActivity();
        onView(withText(R.string.addTag)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), closeSoftKeyboard());

        onView(withText("Ok")).perform(click());
        onView(withText("Cancel")).perform(click());

        onView(withText("The tag is too long")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test that tries to add a tag with spaces
     */
    public void testAddTagWithSpaces() {
        getActivity();
        onView(withText(R.string.addTag)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("hello world"), closeSoftKeyboard());

        onView(withText("Ok")).perform(click());

        onView(withText("Tag \"hello_world\" added")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test that tries to add a tag with uppercase letters
     */
    public void testAddTagUpperCase() {
        getActivity();
        onView(withText(R.string.addTag)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("HELLO"), closeSoftKeyboard());

        onView(withText("Ok")).perform(click());

        onView(withText("Tag \"hello\" added")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test that tries to add a valid tag
     */
    public void testAddTag() {
        getActivity();
        onView(withText(R.string.addTag)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("nice"), closeSoftKeyboard());

        onView(withText("Ok")).perform(click());

        onView(withText("Tag \"nice\" added")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Test that tries to leave the group
     */
    public void testLeaveGroup() {
        getActivity();
        onView(withText(R.string.leaveGroup)).perform(click());

        onView(withText("Ok")).perform(click());

        onView(withId(R.id.actionDiscover)).check(matches(isDisplayed()));
    }
}
