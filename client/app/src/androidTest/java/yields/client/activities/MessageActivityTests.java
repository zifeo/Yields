package yields.client.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import yields.client.R;
import yields.client.id.Id;
import yields.client.messages.MessageView;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests which test the MessageActivity (display and interaction).
 */
@RunWith(AndroidJUnit4.class)
public class MessageActivityTests extends ActivityInstrumentationTestCase2<MessageActivity> {

    private static final Group MOCK_GROUP = MockFactory.createMockGroup("Mock group", new Id(11111), new ArrayList<Node>());

    public MessageActivityTests() {
        super(MessageActivity.class);
    }

    /**
     * Set up for the tests.
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getContext());
        YieldsApplication.setGroup(MOCK_GROUP);
        Bitmap image1 = BitmapFactory.decodeResource(YieldsApplication.getResources(), R.drawable.userpicture);
        ClientUser MOCK_CLIENT_USER =  MockFactory.generateFakeClientUser("Mock client user", new Id(117), "Mock email client user", image1);
        YieldsApplication.setUser(MOCK_CLIENT_USER);
    }

    /**
     * Test if the starting display for that activity is correct. Given a 4 text messages
     */
    @Test
    public void testStartingDisplay(){
        Activity messageActivity = getActivity();

        //Group info
        TextView groupName = (TextView) messageActivity.findViewById(R.id.groupName);
        assertEquals(MOCK_GROUP.getName(), groupName.getText());

        //Input field
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        assertTrue(inputMessageField.getText().length() == 0);

        //Messages
        ListView listView = (ListView) messageActivity.findViewById(R.id.messageScrollLayout);

        for(int i = 0; i < listView.getAdapter().getCount(); i ++){
            String userName = "Mock user " + i;
            String email = "Mock email " + i;
            String nodeName =  "Mock node name " + i;
            Id id = new Id(-i);
            String textContent = "Mock message #" + i;

            MessageView messageView = (MessageView) listView.getChildAt(i);

            //Sender Info
            assertEquals(userName, messageView.getMessage().getSender().getName());
            assertEquals(email, messageView.getMessage().getSender().getEmail());

            //Node Info
            assertEquals(nodeName, messageView.getMessage().getName());
            assertEquals(id.getId(), messageView.getMessage().getId().getId());

            //Content Info
            assertEquals("text", messageView.getMessage().getContent().getType());
            assertEquals(textContent,((TextContent)messageView.getMessage().getContent()).getText());
        }
        messageActivity.finish();
    }

    /**
     * Tests if a text message written by the client user shows up correctly.
     * And if the input field is then empty.
     */
    @Test
    public void testWrittenTextMessageIsCorrect(){
        Activity messageActivity = getActivity();
        onView(withId(R.id.inputMessageField)).perform(typeText("Mock input message 1"));
        onView(withId(R.id.sendButton)).perform(click());
        ListView listView = (ListView) messageActivity.findViewById(R.id.messageScrollLayout);
        int i = listView.getChildCount();
        MessageView messageView = (MessageView) listView.getChildAt(i-1);
        assertEquals("Mock input message 1",
                ((TextContent) messageView.getMessage().getContent()).getText());
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        assertTrue(inputMessageField.getText().length() == 0);
    }

    /**
     * Tests if the inputField autoscrolls to the bottom for a long input
     */
    @Test
    public void testIfInputFieldAutoscrolls(){
        Activity messageActivity = getActivity();
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        String input = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod" +
                " tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
                "culpa qui officia deserunt mollit anim id est laborum";
        onView(withId(R.id.inputMessageField)).perform(typeText(input));
        assertTrue(0 < inputMessageField.getScrollY());
        inputMessageField.clearComposingText();
    }
}
