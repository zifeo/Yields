package yields.client.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

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

public class SelectUsernameActivityTest extends ActivityInstrumentationTestCase2<SelectUsernameActivity> {
    public SelectUsernameActivityTest() {
        super(SelectUsernameActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testCannotEnterTooShortUsername(){
        getActivity();
        onView(withId(R.id.editTextCreateAccount)).perform(typeText(""));
        onView(withId(R.id.buttonCreateAccount)).perform(click());

        onView(withText(R.string.messageUsernameTooShort)).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    public void testCannotEnterSpaces() {
        getActivity();
        onView(withId(R.id.editTextCreateAccount)).perform(typeText("John Doe"));
        onView(withId(R.id.buttonCreateAccount)).perform(click());

        onView(withText(R.string.messageUsernameContainsSpaces)).inRoot(withDecorView(not(is(getActivity().
                getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}
