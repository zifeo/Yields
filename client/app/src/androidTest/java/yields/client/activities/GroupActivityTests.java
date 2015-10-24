package yields.client.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import yields.client.R;

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

public class GroupActivityTests extends ActivityInstrumentationTestCase2<GroupActivity> {
    public GroupActivityTests() {
        super(GroupActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    @LargeTest
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
}
