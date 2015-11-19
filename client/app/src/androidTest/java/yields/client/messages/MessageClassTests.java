package yields.client.messages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import yields.client.R;
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
        MOCK_TEXT_CONTENT_1  = MockFactory.generateFakeTextContent("Mock text.");
    }

    /**
     * Tests a TextContent's getType() method.
     */
    @Test
    public void testTextContentHasCorrectType(){
        assertEquals(Content.ContentType.TEXT, MOCK_TEXT_CONTENT_1.getType());
    }

    /**
     * Tests a ImageContent's getType() method.
     */
    @Test
    public void testImageContentHasCorrectType(){
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.send_icon);
        ImageContent imageContent = MockFactory.generateFakeImageContent(bitmap, "Mock caption");
        assertEquals(Content.ContentType.IMAGE, imageContent.getType());
    }

    /**
     * Tests a TextContent's getText() method.
     */
    @Test
    public void testTextContentReturnsCorrectText(){
        assertEquals("Mock text.", MOCK_TEXT_CONTENT_1.getText());
    }

    /**
     * Test an ImageContent's getCaption() method.
     */
    @Test
    public void testImageContentReturnsCorrectCaption(){
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.send_icon);
        ImageContent imageContent = MockFactory.generateFakeImageContent(bitmap, "Mock caption");
        assertEquals("Mock caption", imageContent.getCaption());
    }

    /**
     * Tests an ImageContent's getImage() method.
     */
    @Test
    public void testImageContentReturnsCorrectImage(){
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.send_icon);
        ImageContent imageContent = MockFactory.generateFakeImageContent(bitmap, "Mock caption");
        assertEquals(bitmap, imageContent.getImage());
    }

    /**
     * Tests a TextContent's getView() method.
     */
    @Test
    public void testTextContentReturnsCorrectLayout(){
        View view = MOCK_TEXT_CONTENT_1.getView();
        TextView text = (TextView) view;
        assertEquals("Mock text.", text.getText());
    }

    /**
     * Tests an ImageContent's getView() method.
     */
    @Test
    public void testImageContentReturnsCorrectLayout(){
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.send_icon);
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

    @Test
    public void testMessagesFromJSONAreCorrectlyParsedForSender() throws JSONException, ParseException {
        Message m = new Message(new JSONArray(JSON_MESSAGE));
        User u = m.getSender();
        assertEquals((new Id("117")).getId(), u.getId().getId());
    }

    @Test
    public void testMessagesFromJSONAreCorrectlyParserForDate() throws JSONException, ParseException {
        Message m = new Message(new JSONArray(JSON_MESSAGE));
        Date date = m.getDate();
        assertEquals(DateSerialization.toDate("2015-11-17T00:30:16.276+01:00").toString(), date.toString());
    }

    @Test
    public void testMessagesFromJSONAreCorrectlyParserForContent() throws JSONException, ParseException {
        Message m = new Message(new JSONArray(JSON_MESSAGE));
        TextContent content = (TextContent) m.getContent();
        assertEquals("MESSAGE_TEXT", content.getText());
    }

    /**
     * Tests if a MessageView is buil
     */
    @Test
    public void testMessageViewReturnsCorrectMessage(){
      //TODO : find a way to inflate xml from junit test
    }
}
