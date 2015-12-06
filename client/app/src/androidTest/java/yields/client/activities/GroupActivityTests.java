package yields.client.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import yields.client.R;
import yields.client.generalhelpers.MockModel;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.node.Group;
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

public class GroupActivityTests extends ActivityInstrumentationTestCase2<GroupActivity> {
    public GroupActivityTests() {
        super(GroupActivity.class);

        ServiceTestConnection.connectActivityToService();
    }

    /**
     * Set up for the tests.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        new MockModel();
        getActivity();
    }

    @Override
    public void tearDown() {
        YieldsApplication.cancelToast();
    }

    /**
     * Test that runs through all activities related to group creation
     */
    public void testGroupCreation() {

        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("SWENG discussion"), closeSoftKeyboard());
        onView(withId(R.id.actionDoneSelectName)).perform(click());

        onView(withId(R.id.actionDoneCreateGroup)).perform(click());

        boolean found = false;
        ListView listView = (ListView) getActivity().findViewById(R.id.listViewGroups);
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View v = listView.getChildAt(i);
            TextView groupName = (TextView) v.findViewById(R.id.textViewGroupName);
            String textGroupName = groupName.getText().toString();

            if (textGroupName.equals("SWENG discussion")) {
                found = true;
            }
        }

        assertTrue("Group not found in list", found);
    }

    /**
     * Test that runs through all activities related to group creation, including adding users
     */
    public void testGroupCreationWithContactAdded() {
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("SWENG discussion2"), closeSoftKeyboard());
        onView(withId(R.id.actionDoneSelectName)).perform(click());

        onView(withId(R.id.actionAddContactToGroup)).perform(click());

        onView(withId(R.id.actionDoneSelectUser)).perform(click());

        onView(withId(R.id.actionDoneCreateGroup)).perform(click());

        boolean found = false;
        ListView listView = (ListView) getActivity().findViewById(R.id.listViewGroups);
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View v = listView.getChildAt(i);
            TextView groupName = (TextView) v.findViewById(R.id.textViewGroupName);
            String textGroupName = groupName.getText().toString();

            if (textGroupName.equals("SWENG discussion2")) {
                found = true;
            }
        }

        assertTrue("Group not found in list", found);
    }

    /**
     * Test that runs through all activities related to group creation, and set the new group
     * to public
     */
    public void testGroupCreationSetPrivateVisibility() {
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("Private Group"), closeSoftKeyboard());

        onView(withId(R.id.radioButtonPrivateGroup)).perform(click());
        onView(withId(R.id.actionDoneSelectName)).perform(click());

        onView(withId(R.id.actionDoneCreateGroup)).perform(click());

        ListView listView = (ListView) getActivity().findViewById(R.id.listViewGroups);
        boolean found = false;
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View v = listView.getChildAt(i);
            TextView groupName = (TextView) v.findViewById(R.id.textViewGroupName);
            String textGroupName = groupName.getText().toString();

            if (textGroupName.equals("Private Group")) {
                found = true;
                Group group = (Group) listView.getAdapter().getItem(i);
                assertEquals(Group.GroupVisibility.PRIVATE, group.getVisibility());
            }
        }
        if (!found) {
            fail("Error group not found");
        }
    }

    /**
     * Test that runs through all activities related to group creation, flipping
     * through public and private, and set the new group to public
     */
    public void testGroupCreationFlipBetweenVisibilityButtonPublic() {
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("Public Group"), closeSoftKeyboard());

        onView(withId(R.id.radioButtonPublicGroup)).perform(click());
        onView(withId(R.id.radioButtonPrivateGroup)).perform(click());
        onView(withId(R.id.radioButtonPublicGroup)).perform(click());
        onView(withId(R.id.actionDoneSelectName)).perform(click());

        onView(withId(R.id.actionDoneCreateGroup)).perform(click());

        ListView listView = (ListView) getActivity().findViewById(R.id.listViewGroups);
        boolean found = false;
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View v = listView.getChildAt(i);
            TextView groupName = (TextView) v.findViewById(R.id.textViewGroupName);
            String textGroupName = groupName.getText().toString();

            if (textGroupName.equals("Public Group")) {
                found = true;
                Group group = (Group) listView.getAdapter().getItem(i);
                assertEquals(Group.GroupVisibility.PUBLIC, group.getVisibility());
            }
        }
        if (!found) {
            fail("Error group not found");
        }
    }

    /**
     * Test that runs through all activities related to group creation, flipping
     * through public and private, and set the new group to private
     */
    public void testGroupCreationFlipBetweenVisibilityButtonPrivate() {
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("Private Group"), closeSoftKeyboard());

        onView(withId(R.id.radioButtonPrivateGroup)).perform(click());
        onView(withId(R.id.radioButtonPublicGroup)).perform(click());
        onView(withId(R.id.radioButtonPrivateGroup)).perform(click());
        onView(withId(R.id.actionDoneSelectName)).perform(click());

        onView(withId(R.id.actionDoneCreateGroup)).perform(click());

        ListView listView = (ListView) getActivity().findViewById(R.id.listViewGroups);
        boolean found = false;
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View v = listView.getChildAt(i);
            TextView groupName = (TextView) v.findViewById(R.id.textViewGroupName);
            String textGroupName = groupName.getText().toString();

            if (textGroupName.equals("Private Group")) {
                found = true;
                Group group = (Group) listView.getAdapter().getItem(i);
                assertEquals(Group.GroupVisibility.PRIVATE, group.getVisibility());
            }
        }
        if (!found) {
            fail("Error group not found");
        }
    }

    /**
     * Test that runs through all activities related to group creation,
     * and creating a rss feed with a malformed url
     */
    public void testGroupCreationRSSMalformed() {
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("RSS !"), closeSoftKeyboard());

        onView(withId(R.id.radioButtonRss)).perform(click());
        onView(withId(R.id.actionDoneSelectName)).perform(click());

        onView(withId(R.id.editTextUrl)).perform(clearText(), typeText("rss"), closeSoftKeyboard());
        onView(withId(R.id.actionDoneCreateRss)).perform(click());

        onView(withText(R.string.messageUrlHttp)).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}
