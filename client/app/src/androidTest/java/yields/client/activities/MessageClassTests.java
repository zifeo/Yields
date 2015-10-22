package yields.client.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;

import yields.client.R;
import yields.client.exceptions.ContentException;
import yields.client.messages.ImageContent;
import yields.client.messages.TextContent;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Tests which test the methods of the classes in the messages package.
 */
public class MessageClassTests extends ActivityInstrumentationTestCase2<MessageActivity> {

    private static final TextContent MOCK_TEXT_CONTENT_1 = MockFactory.generateFakeTextContent("Mock text.");

    public MessageClassTests() {
        super(MessageActivity.class);
    }

    /**
     * Set up for the tests.
    */
    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getContext());
        getInstrumentation().getContext().getResources();
    }

    /**
     * Tests a TextContent's getType() method.
     */
    @Test
    public void testTextContentHasCorrectType(){
        assertEquals("text", MOCK_TEXT_CONTENT_1.getType());
    }

    /**
     * Tests a ImageContent's getType() method.
     */
    @Test
    public void testImageContentHasCorrectType(){
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.send_icon);
        ImageContent imageContent = MockFactory.generateFakeImageContent(bitmap, "Mock caption");
        assertEquals("image", imageContent.getType());
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

    /**
     * Tests if a MessageView is buil
     */
    @Test
    public void testMessageViewReturnsCorrectMessage(){
      //TODO : find a way to inflate xml from junit test
    }
}
