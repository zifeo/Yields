package yields.client.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import yields.client.R;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.id.Id;
import yields.client.node.ClientUser;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests for SearchGroupActivity
 */
public class SearchGroupActivityTests extends ActivityInstrumentationTestCase2<GroupActivity> {
    public SearchGroupActivityTests() {
        super(GroupActivity.class);

        ServiceTestConnection.connectActivityToService();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        YieldsApplication.setUser(new ClientUser("Poto", new Id(1), "a@a.a",
                YieldsApplication.getDefaultUserImage()));
    }

    /**
     * Simple test which just goes to SearchGroupActivity
     */
    public void testGoToSearch() {
        getActivity();
        onView(withId(R.id.actionDiscover)).perform(click());

        onView(withId(R.id.textViewInfoSearch)).check(matches(withText(R.string.startSearchInfo)));
    }
}
