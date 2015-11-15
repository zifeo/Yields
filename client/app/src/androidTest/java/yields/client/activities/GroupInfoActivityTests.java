package yields.client.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import java.util.ArrayList;

import yields.client.R;
import yields.client.id.Id;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests for GroupInfoActivity
 */
public class GroupInfoActivityTests extends ActivityInstrumentationTestCase2<GroupInfoActivity> {
    public GroupInfoActivityTests() {
        super(GroupInfoActivity.class);
    }

    /**
     * Set up for the tests
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    /**
     * Test that the correct name is displayed
     */
    public void testCorrectName(){
        Group g = new Group("Kapoue", new Id(123), new ArrayList<User>());
        YieldsApplication.setGroup(g);
        getActivity();

        onView(withId(R.id.textViewGroupName)).check(matches(withText("Kapoue")));
    }

    /**
     * Test that the correct, unique tag, is displayed
     */
    public void testCorrectNoTags(){
        Group g = new Group("Kapoue", new Id(123), new ArrayList<User>());
        YieldsApplication.setGroup(g);
        getActivity();

        onView(withId(R.id.textViewTags)).check(matches(withText(R.string.noTags)));
    }

    /**
     * Test that the correct, unique tag, is displayed
     */
    public void testCorrectTag(){
        Group g = new Group("Kapoue", new Id(123), new ArrayList<User>());
        g.addTag(new Group.Tag("fun"));

        YieldsApplication.setGroup(g);
        getActivity();

        onView(withId(R.id.textViewTags)).check(matches(withText("Tag : fun")));
    }

    /**
     * Test that the correct tags are displayed
     */
    public void testCorrectTags(){
        Group g = new Group("Kapoue", new Id(123), new ArrayList<User>());
        g.addTag(new Group.Tag("fun"));
        g.addTag(new Group.Tag("happy"));

        YieldsApplication.setGroup(g);
        getActivity();

        String tags = ((TextView) getActivity().findViewById(R.id.textViewTags)).getText().toString();

        assertTrue(tags.startsWith("Tags : "));
        assertTrue(tags.contains("fun"));
        assertTrue(tags.contains("happy"));
    }
}
