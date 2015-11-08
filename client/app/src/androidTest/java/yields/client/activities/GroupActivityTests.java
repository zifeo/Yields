package yields.client.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Test;

import yields.client.R;
import yields.client.node.Group;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class GroupActivityTests extends ActivityInstrumentationTestCase2<GroupActivity> {
    public GroupActivityTests() {
        super(GroupActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testGroupCreation(){
        getActivity();
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("SWENG discussion"));
        onView(withId(R.id.actionDoneSelectName)).perform(click());

        onView(withId(R.id.actionDoneCreateGroup)).perform(click());

        boolean found = false;
        ListView listView = (ListView) getActivity().findViewById(R.id.listViewGroups);
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View v = listView.getChildAt(i);
            TextView groupName = (TextView) v.findViewById(R.id.textViewGroupName);
            String textGroupName = groupName.getText().toString();

            if (textGroupName.equals("SWENG discussion")){
                found = true;
            }
        }

        assertTrue("Group not found in list", found);
    }

    public void testGroupCreationWithContactAdded(){
        getActivity();
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("SWENG discussion2"));
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

            if (textGroupName.equals("SWENG discussion2")){
                found = true;
            }
        }

        assertTrue("Group not found in list", found);
    }

    public void testGroupCreationSetPublicVisibility(){
        getActivity();
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("Public Group"));
        onView(withId(R.id.actionDoneSelectName)).perform(click());

        onView(withId(R.id.actionDoneCreateGroup)).perform(click());

        ListView listView = (ListView) getActivity().findViewById(R.id.listViewGroups);
        boolean found = false;
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View v = listView.getChildAt(i);
            TextView groupName = (TextView) v.findViewById(R.id.textViewGroupName);
            String textGroupName = groupName.getText().toString();

            if (textGroupName.equals("Public Group")){
                found = true;
                Group group = (Group) listView.getAdapter().getItem(i);
                assertEquals(Group.GroupVisibility.PUBLIC, group.getVisibility());
            }
        }
        if (!found){
            fail("Error group not found");
        }
    }

    public void testGroupCreationSetPrivateVisibility(){
        getActivity();
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("Private Group"));
        onView(withId(R.id.radioButtonPrivateGroup)).perform(click());
        onView(withId(R.id.actionDoneSelectName)).perform(click());

        onView(withId(R.id.actionDoneCreateGroup)).perform(click());

        ListView listView = (ListView) getActivity().findViewById(R.id.listViewGroups);
        boolean found = false;
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View v = listView.getChildAt(i);
            TextView groupName = (TextView) v.findViewById(R.id.textViewGroupName);
            String textGroupName = groupName.getText().toString();

            if (textGroupName.equals("Private Group")){
                found = true;
                Group group = (Group) listView.getAdapter().getItem(i);
                assertEquals(Group.GroupVisibility.PRIVATE, group.getVisibility());
            }
        }
        if (!found){
            fail("Error group not found");
        }
    }

    public void testGroupCreationFlipBetweenVisibilityButtonPublic(){
        getActivity();
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("Public Group"));
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

            if (textGroupName.equals("Public Group")){
                found = true;
                Group group = (Group) listView.getAdapter().getItem(i);
                assertEquals(Group.GroupVisibility.PUBLIC, group.getVisibility());
            }
        }
        if (!found){
            fail("Error group not found");
        }
    }

    public void testGroupCreationFlipBetweenVisibilityButtonPrivate(){
        getActivity();
        onView(withId(R.id.actionCreate)).perform(click());

        onView(withId(R.id.editTextSelectGroupName)).perform(typeText("Private Group"));
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

            if (textGroupName.equals("Private Group")){
                found = true;
                Group group = (Group) listView.getAdapter().getItem(i);
                assertEquals(Group.GroupVisibility.PRIVATE, group.getVisibility());
            }
        }
        if (!found){
            fail("Error group not found");
        }
    }
}
