package yields.client.messages;

import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import yields.client.activities.MessageActivity;
import yields.client.activities.MockFactory;
import yields.client.exceptions.ContentException;
import yields.client.id.Id;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Tests which test the methods of the classes in the messages package.
 */
public class MessageClassTests extends ActivityInstrumentationTestCase2<MessageActivity> {

    private static TextContent MOCK_TEXT_CONTENT_1;
    private static final String JSON_MESSAGE = "{\"datetime\": \"2011-12-03T10:15:30+01:00\", \"node\":\"null\", \"text\":\"MESSAGE_TEXT\", \"user\":\"117\", \"id\":\"2\"}";

    public MessageClassTests() {
        super(MessageActivity.class);
    }

    /**
     * Set up for the tests.
     */
    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getTargetContext());
        YieldsApplication.setResources(getInstrumentation().getContext().getResources());
        MOCK_TEXT_CONTENT_1 = MockFactory.generateFakeTextContent("Mock text.");
    }

    /**
     * Tests a Message's getSender() method.
     */
    @Test
    public void testMessageHasCorrectSender() {
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Message message = new Message("message", new Id(-4), user,
                MockFactory.generateFakeTextContent(1), new Date());
        assertEquals(user, message.getSender());
    }

    /**
     * Tests a Message's getDate() method.
     */
    @Test
    public void testMessageHasCorrectDate() {
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Date date = new Date();
        Message message = new Message("message", new Id(-4), user,
                MockFactory.generateFakeTextContent(1), date);
        assertEquals(0, message.getDate().compareTo(date));
    }

    /**
     * Tests a Message's getContent() method.
     */
    @Test
    public void testMessageHasCorrectContent() {
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Content content = MockFactory.generateFakeTextContent(1);
        Message message = new Message("message", new Id(-4), user, content, new Date());
        assertEquals(content, message.getContent());
    }

    /**
     * Tests a Message's getStatus() method.
     */
    @Test
    public void testMessageHasCorrectStatus() {
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Content content = MockFactory.generateFakeTextContent(1);
        Message message = new Message("message", new Id(-4), user, content, new Date(),
                Message.MessageStatus.NOT_SENT);
        assertEquals(Message.MessageStatus.NOT_SENT, message.getStatus());

        message = new Message("message", new Id(-4), user, content, new Date(),
                Message.MessageStatus.RECEIVED);
        assertEquals(Message.MessageStatus.RECEIVED, message.getStatus());

        message = new Message("message", new Id(-4), user, content, new Date(),
                Message.MessageStatus.SEEN);
        assertEquals(Message.MessageStatus.SEEN, message.getStatus());
    }

    /**
     * Tests a TextContent's getType() method.
     */
    @Test
    public void testTextContentHasCorrectType() {
        assertEquals(Content.ContentType.TEXT, MOCK_TEXT_CONTENT_1.getType());
    }

    /**
     * Tests a ImageContent's getType() method.
     */
    @Test
    public void testImageContentHasCorrectType() {
        Bitmap bitmap = YieldsApplication.getDefaultGroupImage();
        ImageContent imageContent = MockFactory.generateFakeImageContent(bitmap, "Mock caption");
        assertEquals(Content.ContentType.IMAGE, imageContent.getType());
    }

    /**
     * Tests a TextContent's getText() method.
     */
    @Test
    public void testTextContentReturnsCorrectText() {
        assertEquals("Mock text.", MOCK_TEXT_CONTENT_1.getText());
    }

    /**
     * Test an ImageContent's getCaption() method.
     */
    @Test
    public void testImageContentReturnsCorrectCaption() {
        Bitmap bitmap = YieldsApplication.getDefaultGroupImage();
        ImageContent imageContent = MockFactory.generateFakeImageContent(bitmap, "Mock caption");
        assertEquals("Mock caption", imageContent.getCaption());
    }

    /**
     * Tests an ImageContent's getImage() method.
     */
    @Test
    public void testImageContentReturnsCorrectImage() {
        Bitmap bitmap = YieldsApplication.getDefaultGroupImage();
        ImageContent imageContent = MockFactory.generateFakeImageContent(bitmap, "Mock caption");
        assertTrue(imageContent.getImage().sameAs(bitmap));
    }

    /**
     * Tests a TextContent's getView() method.
     */
    @Test
    public void testTextContentReturnsCorrectLayout() {
        View view = MOCK_TEXT_CONTENT_1.getView();
        TextView text = (TextView) view;
        assertEquals("Mock text.", text.getText());
    }

    /**
     * Tests an ImageContent's getView() method.
     */
    @Test
    public void testImageContentReturnsCorrectLayout() {
        Bitmap bitmap = YieldsApplication.getDefaultGroupImage();
        ImageContent imageContent = MockFactory.generateFakeImageContent(bitmap, "Mock caption");
        LinearLayout view = null;
        try {
            view = (LinearLayout) imageContent.getView();
        } catch (ContentException e) {
            e.printStackTrace();
        }
        //Caption from TextView
        TextView caption = (TextView) view.getChildAt(0);
        assertEquals("Mock caption", caption.getText());
        //Image from ImageView
        ImageView image = (ImageView) view.getChildAt(1);
        //TODO : find a way to check that layout image is correct
    }

    /**
     * Tests Message's constructor that takes a String (JSON format).
     */
    @Test
    public void testMessagesFromJSONAreCorrectlyParseD() {
        Message m = null;
        try {
            m = new Message(new JSONObject(JSON_MESSAGE));
        } catch (JSONException e) {
            fail("Couldn't parse JSON into Message");
        }
        User u = m.getSender();
        assertEquals((new Id("117")).getId(), u.getId().getId());
        Date date = m.getDate();
        try {
            assertEquals(DateSerialization.toDate("2011-12-03T10:15:30+01:00").toString(), date.toString());
        } catch (ParseException e) {
            fail("Couldn't deserialize Message's Date");
        }
        TextContent content = (TextContent) m.getContent();
        assertEquals("MESSAGE_TEXT", content.getText());
    }
}
