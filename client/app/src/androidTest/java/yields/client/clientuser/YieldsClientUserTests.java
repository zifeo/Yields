package yields.client.clientuser;

import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import yields.client.R;
import yields.client.activities.GroupActivity;
import yields.client.activities.MockFactory;
import yields.client.id.Id;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.serverconnection.Request;
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
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getTargetContext());
        YieldsApplication.setResources(getInstrumentation().getContext().getResources());
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

    @Test
    public void testTextMessageRequestIsCorrectlyParsed(){
        Group mockGroup = MockFactory.createMockGroup("Mock group", new Id(117), new ArrayList<User>());
        TextContent mockContent = MockFactory.generateFakeTextContent("Hi, how are you ?");
        Message message = MockFactory.generateMockMessage("node name", new Id(2), YieldsApplication.getUser(), mockContent);

        Request req = YieldsClientUser.createRequestForMessageToSend(mockGroup, message);
        // TODO : verify the parsed json ...
    }

    @Test
    public void testImageMessageRequestIsCorrectlyParsed(){
        Group mockGroup = MockFactory.createMockGroup("Mock group", new Id(117), new ArrayList<User>());
        ImageContent mockContent = MockFactory.generateFakeImageContent(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.userpicture), "Caption");
        Message message = MockFactory.generateMockMessage("node name", new Id(2), YieldsApplication.getUser(), mockContent);

        Request req = YieldsClientUser.createRequestForMessageToSend(mockGroup, message);
        // TODO : verify the parsed json ...
    }
}