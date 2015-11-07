package yields.client.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Test;

import java.util.ArrayList;

import yields.client.R;
import yields.client.id.Id;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class GroupSettingsActivityTests extends ActivityInstrumentationTestCase2<GroupSettingsActivity> {
    public GroupSettingsActivityTests() {
        super(GroupSettingsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        ClientUser user = MockFactory.generateFakeClientUser("User", new Id(123), "a@b.c", YieldsApplication.getDefaultUserImage());
        user.addUserToEntourage(MockFactory.generateFakeClientUser("Friend", new Id(125), "a@b.d", YieldsApplication.getDefaultUserImage()));

        YieldsApplication.setUser(user);
        YieldsApplication.setGroup(new Group("Group", new Id(124), new ArrayList<User>()));
    }

    public void testChangeName(){
        getActivity();
        onView(withText(R.string.changeGroupName)).perform(click());
        onView(withId(R.id.editText)).perform(typeText(" SWENG"));

        onView(withText("Ok")).perform(click());


        onView(withText("Group name changed to \"Group SWENG\"")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    public void testChangeType(){
        getActivity();
        onView(withText(R.string.changeGroupType)).perform(click());
        onView(withText(" Private")).perform(click());

        onView(withText("Ok")).perform(click());

        onView(withText("Group type changed to : private")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    public void testAddUsers(){
        getActivity();
        onView(withText(R.string.addUsers)).perform(click());
        onView(withId(R.id.actionDoneSelectUser)).perform(click());

        onView(withText("0 user(s) added to group")).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}
