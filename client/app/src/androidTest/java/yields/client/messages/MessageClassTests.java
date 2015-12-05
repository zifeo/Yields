package yields.client.messages;

import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import yields.client.activities.MessageActivity;
import yields.client.activities.MockFactory;
import yields.client.exceptions.ContentException;
import yields.client.generalhelpers.MockModel;
import yields.client.id.Id;
import yields.client.node.ClientUser;
import yields.client.node.User;
import yields.client.serverconnection.DateSerialization;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Tests which test the methods of the classes in the messages package.
 */
public class MessageClassTests extends ActivityInstrumentationTestCase2<MessageActivity> {

    private static TextContent MOCK_TEXT_CONTENT_1;
    private static final String JSON_MESSAGE = "[\"2015-11-17T00:30:16.276+01:00\", \"117\", \"null\", \"MESSAGE_TEXT\" ]";

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
        new MockModel();
        MOCK_TEXT_CONTENT_1 = new TextContent("Mock text.");
    }

    /**
     * Tests a Message's getSender() method.
     */
    @Test
    public void testMessageHasCorrectSender() {
        YieldsApplication.setUser(new ClientUser("test",new Id(-1), "test@epfl.ch",
            YieldsApplication.getDefaultUserImage()));
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Message message = new Message("message", new Id(-4), user.getId(),
                MockFactory.generateFakeTextContent(1), new Date());
        assertEquals(user.getId(), message.getSender());
    }

    /**
     * Tests a Message's getDate() method.
     */
    @Test
    public void testMessageHasCorrectDate() {
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Date date = new Date();
        Message message = new Message("message", new Id(-4), user.getId(),
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
        Message message = new Message("message", new Id(-4), user.getId(), content, new Date());
        assertEquals(content, message.getContent());
    }

    /**
     * Tests a Message's getStatus() method.
     */
    @Test
    public void testMessageHasCorrectStatus() {
        User user = MockFactory.generateFakeUser("Johnny", new Id(-3), "YOLO@hotmail.jpg");
        Content content = MockFactory.generateFakeTextContent(1);
        Message message = new Message("message", new Id(-4), user.getId(), content, new Date(),
                Message.MessageStatus.NOT_SENT);
        assertEquals(Message.MessageStatus.NOT_SENT, message.getStatus());

        message = new Message("message", new Id(-4), user.getId(), content, new Date(),
                Message.MessageStatus.SENT);
        assertEquals(Message.MessageStatus.SENT, message.getStatus());

        message = new Message("message", new Id(-4), user.getId(), content, new Date(),
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
        ImageContent imageContent = new ImageContent(bitmap, "Mock caption");
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
        ImageContent imageContent = new ImageContent(bitmap, "Mock caption");
        assertEquals("Mock caption", imageContent.getCaption());
    }

    /**
     * Tests an ImageContent's getImage() method.
     */
    @Test
    public void testImageContentReturnsCorrectImage() {
        Bitmap bitmap = YieldsApplication.getDefaultGroupImage();
        ImageContent imageContent = new ImageContent(bitmap, "Mock caption");
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
        ImageContent imageContent = new ImageContent(bitmap, "Mock caption");
        LinearLayout view = null;
        try {
            view = (LinearLayout) imageContent.getView();
        } catch (ContentException e) {
            e.printStackTrace();
        }
        //Caption from TextView
        TextView caption = null;
        if (view != null) {
            caption = (TextView) view.getChildAt(0);
        } else {
            fail("View was incorrect !");
        }
        assertEquals("Mock caption", caption.getText());
        //Image from ImageView
        //TODO : find a way to check that layout image is correct
    }

    // Test no Longer of use as we changed parsing method
}
