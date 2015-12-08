package yields.client.activities;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import yields.client.R;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.id.Id;
import yields.client.messages.CommentView;
import yields.client.messages.ImageContent;
import yields.client.messages.MessageView;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static org.hamcrest.Matchers.is;

/**
 * Tests which test the MessageActivity (display and interaction).
 */
@RunWith(AndroidJUnit4.class)
public class MessageActivityTests extends ActivityInstrumentationTestCase2<MessageActivity> {

    private static final Group MOCK_GROUP = MockFactory.createMockGroup("Mock group", new Id(11111), new ArrayList<Id>());

    public MessageActivityTests() {
        super(MessageActivity.class);

        ServiceTestConnection.connectActivityToService();
    }

    /**
     * Set up for the tests.
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        SystemClock.sleep(200);
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        YieldsApplication.setApplicationContext(InstrumentationRegistry.getContext());
        YieldsApplication.setResources(InstrumentationRegistry.getTargetContext().getResources());

        ClientUser MOCK_CLIENT_USER = MockFactory.generateFakeClientUser("Mock client user",
                new Id(117), "Mock email client user", Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565));

        SystemClock.sleep(1000);
        YieldsApplication.setUser(MOCK_CLIENT_USER);
        YieldsApplication.setGroup(MOCK_GROUP);
        assertTrue(YieldsApplication.getUser().getImg() != null);
    }

    /**
     * Test if the starting display for that activity is correct. Given a 4 text messages
     */
    @Test
    public void testStartingDisplay() {
        MessageActivity messageActivity = getActivity();
        System.out.println(messageActivity.isDestroyed());

        //Group info
        ActionBar actionBar = (ActionBar) messageActivity.getSupportActionBar();
        assertEquals(MOCK_GROUP.getName(), actionBar.getTitle());

        //Input field
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        assertTrue(inputMessageField.getText().length() == 0);

        //Messages
        ListView listView = messageActivity.getCurrentFragmentListView();

        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            String userName = "Mock user " + i;
            String email = "Mock email " + i;
            String nodeName = "Mock node name " + i;
            Id id = new Id(-i);
            String textContent = "Mock message #" + i;

            MessageView messageView = (MessageView) listView.getChildAt(i);

            //Sender Info
            assertEquals(id, messageView.getMessage().getSender());

            //Node Info
            assertEquals(nodeName, messageView.getMessage().getName());
            assertEquals(id.getId(), messageView.getMessage().getId().getId());

            //Content Info
            assertEquals("text", messageView.getMessage().getContent().getType());
            assertEquals(textContent, ((TextContent) messageView.getMessage().getContent()).getText());
        }
        messageActivity.finish();
    }

    /**
     * Tests if a text message written by the client user shows up correctly.
     * And if the input field is then empty.
     */
    @Test
    public void testWrittenTextMessageIsCorrect() {
        MessageActivity messageActivity = getActivity();
        YieldsApplication.setResources(messageActivity.getResources());
        onView(withId(R.id.inputMessageField)).perform(typeText("Mock input message 1"));
        onView(withId(R.id.sendButton)).perform(click());
        ListView listView = messageActivity.getCurrentFragmentListView();
        int i = listView.getChildCount();
        MessageView messageView = (MessageView) listView.getChildAt(i - 1);
        assertEquals("Mock input message 1",
                ((TextContent) messageView.getMessage().getContent()).getText());
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        assertTrue(inputMessageField.getText().length() == 0);
        messageActivity.finish();
    }

    /**
     * Tests if the inputField autoscrolls to the bottom for a long input
     */
    @Test
    public void testIfInputFieldAutoscrolls() {
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
        messageActivity.finish();
    }

    @Test
    public void testInitialTypeIsGroupMessage() {
        MessageActivity messageActivity = getActivity();
        assertEquals(MessageActivity.ContentType.GROUP_MESSAGES,
                messageActivity.getType());
        messageActivity.finish();
    }

    @Test
    public void testPressingOnMessageChangeType() {
        MessageActivity messageActivity = getActivity();
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        String input = "Mock message #1";
        messageActivity.simulateImageMessage();
        onView(withId(R.id.inputMessageField)).perform(typeText(input));
        YieldsApplication.setGroup(MOCK_GROUP);
        onView(withId(R.id.sendButton)).perform(click());
        Fragment fragment = messageActivity.getCurrentFragment();
        ListView messageList = (ListView) fragment.getView().findViewById(R.id.groupMessageFragmentList);
        int tag = 0;
        messageList.getChildAt(0).setTag((Object) tag);
        View message = messageList.findViewWithTag((Object) tag);
        onView(withTagValue(is((Object) tag))).perform(click());
        assertEquals(MessageActivity.ContentType.MESSAGE_COMMENTS, messageActivity.getType());
        messageActivity.finish();
    }

    @Test
    public void testWrittenCommentIsCorrect() {
        MessageActivity messageActivity = getActivity();
        YieldsApplication.setResources(messageActivity.getResources());
        messageActivity.simulateImageMessage();
        onView(withId(R.id.inputMessageField)).perform(typeText("Mock input message 1"));
        YieldsApplication.setGroup(MOCK_GROUP);
        onView(withId(R.id.sendButton)).perform(click());

        Fragment fragment = messageActivity.getCurrentFragment();
        ListView messageList = (ListView) fragment.getView().findViewById(R.id.groupMessageFragmentList);
        int tag = 0;
        messageList.getChildAt(0).setTag((Object) tag);
        View message = messageList.findViewWithTag((Object) tag);
        onView(withTagValue(is((Object) tag))).perform(click());

        onView(withId(R.id.inputMessageField)).perform(typeText("Mock comment" +
                " message 1"));
        onView(withId(R.id.sendButton)).perform(click());

        fragment = messageActivity.getCurrentFragment();
        ListView listView = (ListView) fragment.getView().findViewById(R.id.commentList);
        int i = listView.getChildCount();
        Log.d("MessageActivityTests", "i = " + i);
        MessageView messageView = (MessageView) listView.getChildAt(i - 1);
        assertEquals("Mock comment message 1", ((TextContent) messageView.getMessage().getContent()).getText());
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        assertTrue(inputMessageField.getText().length() == 0);
        messageActivity.finish();
    }

    @Test
    public void testParentMessageIsCorrect() throws InterruptedException {
        MessageActivity messageActivity = getActivity();
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        String input = "Mock comment";
        messageActivity.simulateImageMessage();
        onView(withId(R.id.inputMessageField)).perform(typeText(input));
        onView(withId(R.id.sendButton)).perform(click());
        Fragment fragment = messageActivity.getCurrentFragment();
        ListView messageList = (ListView) fragment.getView().findViewById(R.id.groupMessageFragmentList);
        int tag = 0;
        messageList.getChildAt(0).setTag((Object) tag);
        View message = messageList.findViewWithTag((Object) tag);
        onView(withTagValue(is((Object) tag))).perform(click());

        fragment = messageActivity.getCurrentFragment();
        LinearLayout messageContainer = (LinearLayout) fragment.getView().findViewById(R.id.messageContainer);

        CommentView commentView = (CommentView) messageContainer.getChildAt(0);
        assertEquals("Mock comment", ((ImageContent) commentView.getMessage().getContent()).getCaption());
        messageActivity.finish();
    }

    @Test
    public void testInputFieldIsFlushedTheSameDuringFragChange() {
        MessageActivity messageActivity = getActivity();
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        String input = "Mock message #1";
        messageActivity.simulateImageMessage();
        onView(withId(R.id.inputMessageField)).perform(typeText(input));
        onView(withId(R.id.sendButton)).perform(click());
        input = "Should be flushed";
        onView(withId(R.id.inputMessageField)).perform(typeText(input));
        Fragment fragment = messageActivity.getCurrentFragment();
        ListView messageList = (ListView) fragment.getView().findViewById(R.id.groupMessageFragmentList);
        int tag = 0;
        messageList.getChildAt(0).setTag((Object) tag);
        View message = messageList.findViewWithTag((Object) tag);
        onView(withTagValue(is((Object) tag))).perform(click());
        assertEquals("", inputMessageField.getText().toString());
        messageActivity.finish();
    }

    @Test
    public void testPressBackButtonReturnsToGroupMessage() throws InterruptedException {
        final MessageActivity messageActivity = getActivity();
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        String input = "Mock message #1";
        messageActivity.simulateImageMessage();
        onView(withId(R.id.inputMessageField)).perform(typeText(input));
        onView(withId(R.id.sendButton)).perform(click());
        Fragment fragment = messageActivity.getCurrentFragment();
        ListView messageList = (ListView) fragment.getView().findViewById(R.id.groupMessageFragmentList);
        int tag = 0;
        messageList.getChildAt(0).setTag((Object) tag);
        View message = messageList.findViewWithTag((Object) tag);
        onView(withTagValue(is((Object) tag))).perform(click());
        assertEquals(MessageActivity.ContentType.MESSAGE_COMMENTS, messageActivity.getType());
        closeSoftKeyboard();
        messageActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageActivity.onBackPressed();
            }
        });
        Thread.sleep(1000);
        assertEquals(MessageActivity.ContentType.GROUP_MESSAGES, messageActivity.getType());
        messageActivity.finish();
    }

    @Test
    public void testCannotSendEmptyTextMessage(){
        final MessageActivity messageActivity = getActivity();
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        onView(withId(R.id.sendButton)).perform(click());
        assertTrue(messageActivity.getCurrentFragmentListView().getAdapter().isEmpty());
        messageActivity.finish();
    }

    @Test
    public void testCaptionForImageIsNotMandatory(){
        final MessageActivity messageActivity = getActivity();
        EditText inputMessageField = (EditText) messageActivity.findViewById(R.id.inputMessageField);
        messageActivity.simulateImageMessage();
        onView(withId(R.id.sendButton)).perform(click());
        assertFalse(messageActivity.getCurrentFragmentListView().getAdapter().isEmpty());
        messageActivity.finish();
    }
}
