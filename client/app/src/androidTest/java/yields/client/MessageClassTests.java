package yields.client;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;

import yields.client.activities.MessageActivity;
import yields.client.exceptions.ContentException;
import yields.client.id.Id;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.MessageView;
import yields.client.messages.TextContent;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;

public class MessageClassTests extends ActivityInstrumentationTestCase2<MessageActivity> {
    private static final TextContent MOCK_TEXT_CONTENT_1 = new TextContent("Mock text.");
    private static final ImageContent MOCK_IMAGE_CONTENT = MockFactory.generateFakeImageContent(null, "Mock caption");
    private static final User MOCK_USER = MockFactory.generateFakeUser("Mock name", new Id(117), "Mock email");
    private static final Message MOCK_MESSAGE = MockFactory.generateMockMessage("Mock node name", new Id(2), MOCK_USER, MOCK_TEXT_CONTENT_1);

    public MessageClassTests() {
        super(MessageActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getContext());
        getInstrumentation().getContext().getResources();
    }

    @Test
    public void testTextContentHasCorrectType(){
        assertEquals("text", MOCK_TEXT_CONTENT_1.getType());
    }

    @Test
    public void testImageContentHasCorrectType(){
        assertEquals("image", MOCK_IMAGE_CONTENT.getType());
    }

    @Test
    public void testTextContentReturnsCorrectText(){
        assertEquals("Mock text.", MOCK_TEXT_CONTENT_1.getText());
    }

    @Test
    public void testImageContentReturnsCorrectCaption(){
        assertEquals("Mock caption", MOCK_IMAGE_CONTENT.getCaption());
    }

    @Test
    public void testImageContentReturnsCorrectImage(){
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.send_icon);
        ImageContent imageContent = MockFactory.generateFakeImageContent(bitmap, "Mock caption");
        assertEquals(bitmap, imageContent.getImage());
    }

    @Test
    public void testTextContentReturnsCorrectLayout(){
        View view = MOCK_TEXT_CONTENT_1.getView();
        TextView text = (TextView) view;
        assertEquals("Mock text.", text.getText());
    }

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
    public void testMessageViewReturnsCorrectMessage(){
      //TODO : find a way to inflate xml from junit test
    }
}
