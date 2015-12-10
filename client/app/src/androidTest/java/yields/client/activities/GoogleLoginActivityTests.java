package yields.client.activities;

import android.content.Intent;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

public class GoogleLoginActivityTests extends ActivityInstrumentationTestCase2<GoogleLoginActivity> {
    public GoogleLoginActivityTests() {
        super(GoogleLoginActivity.class);
    }

    /**
     * Set up for the tests.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    /**
     * TODO: Discuss usefulness of this test... (not working if too many requests)
     * Test that simply launches the activity.
     */
    public void testLaunch(){
        //getActivity();
    }
}
