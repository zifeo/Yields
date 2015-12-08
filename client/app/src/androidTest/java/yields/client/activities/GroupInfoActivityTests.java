package yields.client.activities;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import yields.client.R;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests for GroupInfoActivity
 */
public class GroupInfoActivityTests extends ActivityInstrumentationTestCase2<GroupInfoActivity> {
    public GroupInfoActivityTests() {
        super(GroupInfoActivity.class);

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

        Intent intent = new Intent();
        intent.putExtra(SearchGroupActivity.MODE_KEY, 0);
        setActivityIntent(intent);
    }

    /**
     * Test that the correct name is displayed
     */
    public void testCorrectName(){
        Group g = new Group("Kapoue", new Id(123), new ArrayList<Id>(),
                YieldsApplication.getDefaultGroupImage(), Group.GroupType.PUBLISHER, false, new Date());
        YieldsApplication.setGroup(g);

        getActivity();
        onView(withId(R.id.textViewGroupName)).check(matches(withText("Kapoue")));
    }

    /**
     * Test that no tag is displayed
     */
    public void testCorrectNoTags(){
        Group g = new Group("Kapoue", new Id(123), new ArrayList<Id>(),
                YieldsApplication.getDefaultGroupImage(), Group.GroupType.PUBLISHER, false, new Date());
        YieldsApplication.setGroup(g);
        getActivity();

        onView(withId(R.id.textViewTags)).check(matches(withText(R.string.noTags)));
    }

    /**
     * Test that the correct, unique tag, is displayed
     */
    public void testCorrectTag(){
        Group g = new Group("Kapoue", new Id(123), new ArrayList<Id>(),
                YieldsApplication.getDefaultGroupImage(), Group.GroupType.PUBLISHER, false, new Date());
        g.addTag(new Group.Tag("fun"));

        YieldsApplication.setGroup(g);
        getActivity();

        onView(withId(R.id.textViewTags)).check(matches(withText("Tag : fun")));
    }

    /**
     * Test that the correct tags are displayed
     */
    public void testCorrectTags(){
        Group g = new Group("Kapoue", new Id(123), new ArrayList<Id>(),
                YieldsApplication.getDefaultGroupImage(), Group.GroupType.PUBLISHER, false, new Date());
        g.addTag(new Group.Tag("fun"));
        g.addTag(new Group.Tag("happy"));

        YieldsApplication.setGroup(g);
        getActivity();

        String tags = ((TextView) getActivity().findViewById(R.id.textViewTags)).getText().toString();

        assertTrue(tags.startsWith("Tags : "));
        assertTrue(tags.contains("fun"));
        assertTrue(tags.contains("happy"));
    }

    /**
     * Test that the correct users are displayed
     */
    public void testCorrectUsers(){
        Group g = new Group("Kapoue", new Id(123), new ArrayList<Id>(),
                YieldsApplication.getDefaultGroupImage(), Group.GroupType.PUBLISHER, false, new Date());
        User u1 = new User("Ratchet", new Id(123), "r@veldin.com", YieldsApplication.getDefaultUserImage());
        User u2 = new User("Clank", new Id(121), "c@veldin.com", YieldsApplication.getDefaultUserImage());
        YieldsApplication.getUser().addUserToEntourage(u1);
        YieldsApplication.getUser().addUserToEntourage(u2);
        g.addUser(new Id(121));
        g.addUser(new Id(123));

        YieldsApplication.setGroup(g);
        getActivity();

        onView(withId(R.id.buttonUsers)).perform(click());
        onView(withText("Ratchet")).perform(click());

        onView(withId(R.id.textViewUserName)).check(matches(isDisplayed()));
    }
}
