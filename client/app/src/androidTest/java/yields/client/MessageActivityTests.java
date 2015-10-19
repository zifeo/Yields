package yields.client;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import yields.client.activities.MessageActivity;
import yields.client.id.Id;
import yields.client.messages.Content;
import yields.client.messages.Message;
import yields.client.messages.MessageView;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MessageActivityTests extends ActivityInstrumentationTestCase2<MessageActivity> {
    private static final List<Message> MOCK_MESSAGES = generateMockMessages(4);
    private static final FakeClientUser MOCK_CLIENT_USER = new FakeClientUser("Mock client user", new Id(117), "Mock email client user");
    private static final Group MOCK_GROUP = new Group("Mock group", new Id(11111), new ArrayList<User>());

    public MessageActivityTests() {
        super(MessageActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getContext());
        YieldsApplication.setGroup(MOCK_GROUP);
        YieldsApplication.setUser(MOCK_CLIENT_USER);
    }

    /**
     * Test if the starting display for that activity is correct. Given a 4 text messages
     */
    @Test
    public void testStartingDisplay(){
        Activity messageActivity = getActivity();

        //Group name
        TextView groupName = (TextView) messageActivity.findViewById(R.id.groupName);
        assertEquals(MOCK_GROUP.getName(), groupName.getText());

        //Input field
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        assertTrue(inputMessageField.getText().length() == 0);

        //Messages
        ListView listView = (ListView) messageActivity.findViewById(R.id.messageScrollLayout);
        for(int i = 0; i < MOCK_MESSAGES.size(); i ++){
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
        MessageView messageView = (MessageView) listView.getChildAt(4);
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

    private static class FakeClientUser extends ClientUser{
        public FakeClientUser(String name, Id id, String email) {
            super(name, id, email);
        }

        @Override
        public void sendMessage(Group group, Message message) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Message> getGroupMessages(Group group) {
            return MOCK_MESSAGES;
        }

        @Override
        public void addNewGroup(Group group) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteGroup(Group group) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<User, String> getHistory(Date from) {
            return null;
        }
    }

    private static List<Message> generateMockMessages(int number){
        ArrayList<Message> messages = new ArrayList<>();
        for (int i = 0; i < number; i ++){
            Content content = generateFakeTextContent(i);
            messages.add(new Message("Mock node name " + i, new Id(-i), generateFakeUser(i), content));
        }
        return messages;
    }

    private static Content generateFakeTextContent(int i){
        return new TextContent("Mock message #" + (i));
    }

    private static User generateFakeUser(int i){
        String name = "Mock user " + i;
        String email = "Mock email " + i;
        return new User(name, new Id(i), email);
    }
}
