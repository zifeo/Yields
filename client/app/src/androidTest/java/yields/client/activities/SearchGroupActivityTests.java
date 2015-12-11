package yields.client.activities;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;
import java.util.List;

import yields.client.R;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.id.Id;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests for SearchGroupActivity
 */
public class SearchGroupActivityTests extends ActivityInstrumentationTestCase2<SearchGroupActivity> {
    public SearchGroupActivityTests() {
        super(SearchGroupActivity.class);

        ServiceTestConnection.connectActivityToService();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        YieldsApplication.setUser(new ClientUser("Poto", new Id(1), "a@a.a",
                YieldsApplication.getDefaultUserImage()));

        Intent intent = new Intent();
        intent.putExtra(SearchGroupActivity.MODE_KEY,
                SearchGroupActivity.Mode.SEARCH.ordinal());
        setActivityIntent(intent);

        getActivity();
    }

    /**
     * Test that simulate a search with no result.
     */
    public void testSearchNoResult() {
        YieldsApplication.setGroupsSearched(new ArrayList<Group>());
        getActivity().notifyChange(NotifiableActivity.Change.GROUP_SEARCH);

        onView(withId(R.id.textViewInfoSearch)).check(matches(isDisplayed()));
    }

    /**
     * Test that simulate a search with a result.
     */
    public void testSearchOneResult() {
        List<Group> groupSearched = new ArrayList<Group>();
        groupSearched.add(new Group("FUN", new Id(1), new ArrayList<Id>()));
        YieldsApplication.setGroupsSearched(groupSearched);
        getActivity().notifyChange(NotifiableActivity.Change.GROUP_SEARCH);

        onView(withText("FUN")).perform(click());

        onView(withId(R.id.textViewGroupName)).check(matches(withText("FUN")));
    }
}
