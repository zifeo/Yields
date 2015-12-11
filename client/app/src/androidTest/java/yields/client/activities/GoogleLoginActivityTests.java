package yields.client.activities;

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
     * Test that simply launches the activity.
     * We cannot test anything here unfortunaltey.
     */
    public void testLaunch(){
        getActivity();
    }
}
