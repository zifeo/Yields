package yields.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;

import yields.client.activities.MessageActivity;
import yields.client.id.Id;
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.TextContent;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

import static junit.framework.Assert.assertEquals;

public class MessageClassTests extends ActivityInstrumentationTestCase2<MessageActivity> {
    private static final TextContent MOCK_TEXT_CONTENT_1 = new TextContent("Mock text.");
    private static final ImageContent MOCK_IMAGE_CONTENT = new ImageContent(null, "Mock caption");
    private static final User MOCK_USER = new User("Mock name", new Id(117), "Mock email");
    private static final Message MOCK_MESSAGE = new Message("Mock node name", new Id(2), MOCK_USER, MOCK_TEXT_CONTENT_1);

    public MessageClassTests() {
        super(MessageActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        YieldsApplication.setApplicationContext(InstrumentationRegistry.getContext());
    }

    @Test
    public void testTextContentHasRightType(){
        assertEquals("text", MOCK_TEXT_CONTENT_1.getType());
    }

    @Test
    public void testImageContentHasRightType(){
        assertEquals("image", MOCK_IMAGE_CONTENT.getType());
    }

    @Test
    public void testTextContentReturnsRightText(){
        assertEquals("Mock text.", MOCK_TEXT_CONTENT_1.getText());
    }

    @Test
    public void testImageContentReturnsRightCaption(){
        assertEquals("Mock caption", MOCK_IMAGE_CONTENT.getCaption());
    }

    @Test
    public void testImageContentReturnsRightImage(){
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.send_icon);
        ImageContent imageContent = new ImageContent(bitmap, "Mock caption");
        assertEquals(bitmap, imageContent.getImage());
    }

    @Test
    public void testTextContentReturnRightLayout(){
        View view = MOCK_TEXT_CONTENT_1.getView();
        TextView text = (TextView) view;
        assertEquals("Mock text.", text.getText());
    }
}
