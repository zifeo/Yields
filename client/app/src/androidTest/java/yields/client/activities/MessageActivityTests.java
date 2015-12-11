package yields.client.activities;

import android.app.Activity;
import android.app.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import yields.client.R;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.id.Id;
import yields.client.messages.CommentView;
import yields.client.messages.Content;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.MessageView;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static org.hamcrest.Matchers.is;

/**
 * Tests which test the MessageActivity (display and interaction).
 */
@RunWith(AndroidJUnit4.class)
public class MessageActivityTests extends ActivityInstrumentationTestCase2<MessageActivity> {

    private  final Group MOCK_GROUP = MockFactory.createMockGroup("Mock group", new Id(11111), new ArrayList<Id>());
    private  final ClientUser MOCK_CLIENT_USER = MockFactory.generateFakeClientUser("Mock client user",
            new Id(117), "Mock email client user", Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565));

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
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        YieldsApplication.setApplicationContext(InstrumentationRegistry.getContext());
        YieldsApplication.setResources(InstrumentationRegistry.getTargetContext().getResources());

        SystemClock.sleep(1000);
        YieldsApplication.setUser(MOCK_CLIENT_USER);

        Group mockGroup = MockFactory.createMockGroup("Mock group", new Id(11111), new ArrayList<Id>());
        YieldsApplication.setGroup(mockGroup);
        assertTrue(YieldsApplication.getUser().getImage() != null);

    }

    /**
     * Test if the starting display for that activity is correct. Given a 4 text messages
     */
    @Test
    public void testStartingDisplay() {
        MessageActivity messageActivity = getActivity();
        System.out.println(messageActivity.isDestroyed());

        //Group info
        TextView title = (TextView) messageActivity.findViewById(R.id.toolbarTitle);
        assertEquals(MOCK_GROUP.getName(), title.getText().toString());

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
            assertEquals(id.getId(), messageView.getMessage().getSender().getId());

            //Node Info
            assertEquals(id.getId(), messageView.getMessage().getCommentGroupId().getId());

            //Content Info
            assertEquals("text", messageView.getMessage().getContent().getType());
            assertEquals(textContent, ((TextContent) messageView.getMessage().getContent()).getText());
        }
        messageActivity.finish();
    }

    /**
     * Test that clicks on the group name.
     */
    @Test
    public void testGroupInfo() {
        MessageActivity messageActivity = getActivity();
        onView(withId(R.id.toolbarTitle)).perform(click());
        onView(withId(R.id.textViewGroupName)).check(matches(isDisplayed()));
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
        messageActivity.simulateImageMessage();
        YieldsApplication.setGroup(MOCK_GROUP);
        onView(withId(R.id.inputMessageField)).perform(typeText("Mock input message #1"));
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
        YieldsApplication.setGroup(MOCK_GROUP);
        onView(withId(R.id.inputMessageField)).perform(typeText("Mock input message 1"));
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
    public void testCannotSendEmptyTextMessage() {
        final MessageActivity messageActivity = getActivity();
        onView(withId(R.id.inputMessageField)).perform(typeText(""));
        onView(withId(R.id.sendButton)).perform(click());
        assertTrue(messageActivity.getCurrentFragmentListView().getAdapter().isEmpty());
        messageActivity.finish();
    }

    @Test
    public void testCaptionForImageIsNotMandatory() {
        final MessageActivity messageActivity = getActivity();
        messageActivity.simulateImageMessage();
        onView(withId(R.id.inputMessageField)).perform(typeText(""));
        onView(withId(R.id.sendButton)).perform(click());
        assertFalse(messageActivity.getCurrentFragmentListView().getAdapter().isEmpty());
        messageActivity.finish();
    }

    @Test
    public void testGetCurrentFragment() throws InterruptedException {
        final MessageActivity messageActivity = getActivity();
        String input = "Mock message #1";
        messageActivity.simulateImageMessage();
        onView(withId(R.id.inputMessageField)).perform(typeText(input));
        onView(withId(R.id.sendButton)).perform(click());
        Fragment fragment = messageActivity.getCurrentFragment();
        ListView messageList = (ListView) fragment.getView().findViewById(R.id.groupMessageFragmentList);
        int tag = 0;
        messageList.getChildAt(0).setTag((Object) tag);
        onView(withTagValue(is((Object) tag))).perform(click());
        assertEquals(MessageActivity.ContentType.MESSAGE_COMMENTS, messageActivity.getType());
        closeSoftKeyboard();
        ListView list = messageActivity.getCurrentFragmentListView();
        assertTrue(list.getCount() == 0);
        messageActivity.finish();
    }

    @Test
    public void testGroupIdGetter(){
        final MessageActivity messageActivity = getActivity();
        assertEquals(Long.valueOf(11111), messageActivity.getGroupId().getId());
    }

    @Test
    public void testSendUrlMessage() {
        final MessageActivity messageActivity = getActivity();
        onView(withId(R.id.inputMessageField)).perform(typeText("www.reddit.com"));
        onView(withId(R.id.sendButton)).perform(click());
        Fragment fragment = messageActivity.getCurrentFragment();
        ListView messageList = (ListView) fragment.getView().findViewById(R.id.groupMessageFragmentList);
        Message message = (Message) messageList.getAdapter().getItem(0);
        assertEquals(Content.ContentType.URL, message.getContent().getType());
    }

    @Test
    public void testOnOptionsItemSelectedHome(){
        final MessageActivity messageActivity = getActivity();
        RunnableOnOption runnableOnOption = new RunnableOnOption(messageActivity, android.R.id.home);
        messageActivity.runOnUiThread(runnableOnOption);
        SystemClock.sleep(1000);
        assertEquals(true, runnableOnOption.getReturnedValue());
    }

    @Test
    public void testOnOptionsItemSelectedActionSettingsGroup(){
        final MessageActivity messageActivity = getActivity();
        RunnableOnOption runnableOnOption = new RunnableOnOption(messageActivity, R.id.actionSettingsGroup);
        messageActivity.runOnUiThread(runnableOnOption);
        SystemClock.sleep(1000);
        assertEquals(true, runnableOnOption.getReturnedValue());
    }

    @Test
    public void testOnOptionsItemSelectedActionIconConnect(){
        final MessageActivity messageActivity = getActivity();
        RunnableOnOption runnableOnOption = new RunnableOnOption(messageActivity, R.id.iconConnect);
        messageActivity.runOnUiThread(runnableOnOption);
        SystemClock.sleep(1000);
        assertEquals(true, runnableOnOption.getReturnedValue());
    }

    @Test
    public void testCancelImageSending(){
        final MessageActivity messageActivity = getActivity();
        String input = "Mock message #1";
        messageActivity.simulateImageMessage();
        messageActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageActivity.cancelImageSending(null);
            }
        });
        onView(withId(R.id.inputMessageField)).perform(typeText(input));
        onView(withId(R.id.sendButton)).perform(click());
        Fragment fragment = messageActivity.getCurrentFragment();
        ListView messageList = (ListView) fragment.getView().findViewById(R.id.groupMessageFragmentList);
        Message message = (Message) messageList.getAdapter().getItem(0);
        assertEquals(Content.ContentType.TEXT, message.getContent().getType());
    }

    @Test
    public void testNotifyNewMessages(){
        YieldsApplication.setGroup(MOCK_GROUP);
        final MessageActivity messageActivity = getActivity();
        for (int i = 0 ; i < 30 ; i ++){
            MOCK_GROUP.addMessage(MockFactory.generateMockMessage("", new Id(2), MOCK_CLIENT_USER, new TextContent("topkek")));
            SystemClock.sleep(50);
        }
        assertEquals(0, messageActivity.getCurrentFragmentListView().getCount());
        messageActivity.notifyChange(NotifiableActivity.Change.MESSAGES_RECEIVE);
        SystemClock.sleep(1000);
        assertEquals(30, messageActivity.getCurrentFragmentListView().getCount());
    }

    private MenuItem createMenuItem(final int itemId){
        return new MenuItem() {
            @Override
            public int getItemId() {
                return itemId;
            }

            @Override
            public int getGroupId() {
                return 0;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public MenuItem setTitle(CharSequence title) {
                return null;
            }

            @Override
            public MenuItem setTitle(int title) {
                return null;
            }

            @Override
            public CharSequence getTitle() {
                return null;
            }

            @Override
            public MenuItem setTitleCondensed(CharSequence title) {
                return null;
            }

            @Override
            public CharSequence getTitleCondensed() {
                return null;
            }

            @Override
            public MenuItem setIcon(Drawable icon) {
                return null;
            }

            @Override
            public MenuItem setIcon(int iconRes) {
                return null;
            }

            @Override
            public Drawable getIcon() {
                return null;
            }

            @Override
            public MenuItem setIntent(Intent intent) {
                return null;
            }

            @Override
            public Intent getIntent() {
                return null;
            }

            @Override
            public MenuItem setShortcut(char numericChar, char alphaChar) {
                return null;
            }

            @Override
            public MenuItem setNumericShortcut(char numericChar) {
                return null;
            }

            @Override
            public char getNumericShortcut() {
                return 0;
            }

            @Override
            public MenuItem setAlphabeticShortcut(char alphaChar) {
                return null;
            }

            @Override
            public char getAlphabeticShortcut() {
                return 0;
            }

            @Override
            public MenuItem setCheckable(boolean checkable) {
                return null;
            }

            @Override
            public boolean isCheckable() {
                return false;
            }

            @Override
            public MenuItem setChecked(boolean checked) {
                return null;
            }

            @Override
            public boolean isChecked() {
                return false;
            }

            @Override
            public MenuItem setVisible(boolean visible) {
                return null;
            }

            @Override
            public boolean isVisible() {
                return false;
            }

            @Override
            public MenuItem setEnabled(boolean enabled) {
                return null;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public boolean hasSubMenu() {
                return false;
            }

            @Override
            public SubMenu getSubMenu() {
                return null;
            }

            @Override
            public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
                return null;
            }

            @Override
            public ContextMenu.ContextMenuInfo getMenuInfo() {
                return null;
            }

            @Override
            public void setShowAsAction(int actionEnum) {

            }

            @Override
            public MenuItem setShowAsActionFlags(int actionEnum) {
                return null;
            }

            @Override
            public MenuItem setActionView(View view) {
                return null;
            }

            @Override
            public MenuItem setActionView(int resId) {
                return null;
            }

            @Override
            public View getActionView() {
                return null;
            }

            @Override
            public MenuItem setActionProvider(ActionProvider actionProvider) {
                return null;
            }

            @Override
            public ActionProvider getActionProvider() {
                return null;
            }

            @Override
            public boolean expandActionView() {
                return false;
            }

            @Override
            public boolean collapseActionView() {
                return false;
            }

            @Override
            public boolean isActionViewExpanded() {
                return false;
            }

            @Override
            public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
                return null;
            }
        };
    }

    class RunnableOnOption implements Runnable {
        private boolean mReturnValue;
        private MenuItem mItemId;
        private MessageActivity mActivity;

        public RunnableOnOption(MessageActivity activity, int itemId){
            mItemId = createMenuItem(itemId);
            mActivity = activity;
        }

        @Override
        public void run() {
            mReturnValue = mActivity.onOptionsItemSelected(mItemId);
        }

        public boolean getReturnedValue(){
            return mReturnValue;
        }
    }
}
