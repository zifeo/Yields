package yields.client.clientuser;

import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import yields.client.R;
import yields.client.activities.GroupActivity;
import yields.client.id.Id;
import yields.client.yieldsapplication.YieldsApplication;
import yields.client.yieldsapplication.YieldsClientUser;

public class YieldsClientUserTests extends ActivityInstrumentationTestCase2<GroupActivity> {

    public YieldsClientUserTests(){
        super(GroupActivity.class);
    }

    public YieldsClientUserTests(Class<GroupActivity> activityClass) {
        super(activityClass);
    }

    /**
     * Set up for the tests.
     */
    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getContext());
        getInstrumentation().getContext().getResources();
        YieldsClientUser.createInstance("Mock Client User", new Id(117), "Mock email", BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.userpicture));
    }

    @Test
    public void testYieldsClientUserIsASingleton(){
        try {
            YieldsClientUser.createInstance("Mock Client User", new Id(117), "Mock email", BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.userpicture));
            fail("An exception should have been raised");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
