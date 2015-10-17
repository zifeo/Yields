package yields.client;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Test;

import yields.client.messages.Message;
import yields.client.messages.TextContent;

import static junit.framework.Assert.assertEquals;

public static class MessageClassTests {
    private static final TextContent MOCK_TEXT_CONTENT_1 = new TextContent("Mock text.");
    private static final Message MOCK_MESSAGE_1 = new Message("Mock node name")

    @Test
    public void testTextContentHasRightType(){
        assertEquals("text", MOCK_TEXT_CONTENT_1.getType());
    }

    @Test
    public void testTextContentReturnsRightText(){
        assertEquals("Mock text.", MOCK_TEXT_CONTENT_1.getText());
    }

    @Test
    public void testTextContentReturnRightLayout(){
        LinearLayout layout = MOCK_TEXT_CONTENT_1.getLayout();
        TextView text = (TextView) layout.getChildAt(0);
        assertEquals("Mock text.", text.getText());
    }
}
