package yields.client.messages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import yields.client.R;
import yields.client.activities.MessageActivity;
import yields.client.exceptions.CommentViewException;
import yields.client.generalhelpers.MockModel;
import yields.client.id.Id;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Tests which test the methods of the classes in the messages package.
 */
public class CommentViewTests extends ActivityInstrumentationTestCase2<MessageActivity> {
    private static Date mDate;

    public CommentViewTests() {
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
        YieldsApplication.setDefaultUserImage(Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565));
        new MockModel();
    }

    @Test
    public void testCommentViewMessageGetter(){
        Message message = createMessageForCommentView(Content.ContentType.IMAGE);
        CommentView commentView = new CommentView(YieldsApplication.getApplicationContext(), message);
        assertEquals(message, commentView.getMessage());
    }

    @Test
    public void testCommentCreateViewForTextContent(){
        try {
            Message message = createMessageForCommentView(Content.ContentType.TEXT);
            CommentView commentView = new CommentView(YieldsApplication.getApplicationContext(), message);
            fail("Exception should have been raised.");
        }
        catch (CommentViewException e){
            // Great success.
        }
    }

    @Test
    public void testCommentCreateViewForImageContentImage(){
        Message message = createMessageForCommentView(Content.ContentType.IMAGE);
        CommentView commentView = new CommentView(YieldsApplication.getApplicationContext(), message);
        View view = commentView.getChildAt(0);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageContent);
        Bitmap image = YieldsApplication.getDefaultUserImage();
        WindowManager wm = (WindowManager) YieldsApplication.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        float scalefactor = ((float) (screenWidth)) / image.getWidth();
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, screenWidth, (int)
                (scalefactor * image.getHeight()), false);
        int maxHeight = (int) ((1.f / 4) * size.y);
        if (maxHeight > resizedImage.getHeight()) {
            maxHeight = resizedImage.getHeight();
        }
        resizedImage = Bitmap.createBitmap(resizedImage, 0, 0, resizedImage.getWidth(),
                maxHeight);

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
        Bitmap bitmap = bitmapDrawable .getBitmap();

        assertEquals(resizedImage.getWidth(), bitmap.getWidth());
        assertEquals(resizedImage.getHeight(), bitmap.getHeight());

        for (int x = 0 ; x < resizedImage.getWidth() ; x ++){
            for (int y = 0 ; y < resizedImage.getHeight() ; y ++){
                assertEquals(resizedImage.getPixel(x, y), bitmap.getPixel(x, y));
            }
        }
    }

    @Test
    public void testCommentCreateViewForImageContentCaption() {
        Message message = createMessageForCommentView(Content.ContentType.IMAGE);
        CommentView commentView = new CommentView(YieldsApplication.getApplicationContext(), message);
        View view = commentView.getChildAt(0);
        TextView caption = (TextView) view.findViewById(R.id.caption);
        assertEquals("topkek", caption.getText());
    }

    @Test
    public void testCommentCreateViewForImageContentMessageInfo() {
        Message message = createMessageForCommentView(Content.ContentType.IMAGE);
        CommentView commentView = new CommentView(YieldsApplication.getApplicationContext(), message);
        View view = commentView.getChildAt(0);
        TextView messageinfos = (TextView) view.findViewById(R.id.messageinfos);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(mDate);
        assertEquals("Sent by GCC at " + time, messageinfos.getText());
    }

    @Test
    public void testCommentCreateViewForUrlContentMessageInfo() {
        Message message = createMessageForCommentView(Content.ContentType.URL);
        CommentView commentView = new CommentView(YieldsApplication.getApplicationContext(), message);
        View view = commentView.getChildAt(0);
        TextView messageinfos = (TextView) view.findViewById(R.id.messageinfos);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(mDate);
        assertEquals("Sent by GCC at " + time, messageinfos.getText());
    }

    @Test
    public void testCommentCreateViewForUrlContentCaption(){
        Message message = createMessageForCommentView(Content.ContentType.URL);
        CommentView commentView = new CommentView(YieldsApplication.getApplicationContext(), message);
        View view = commentView.getChildAt(0);

        TextView caption = (TextView) view.findViewById(R.id.caption);
        String coloredCaption = "topkek 9gag.com";
        coloredCaption = coloredCaption.replace("9gag.com", "<font color='#00BFFF'>" + "9gag.com" + "</font>");
        TextView expected = new TextView(YieldsApplication.getApplicationContext());
        expected.setText(Html.fromHtml(coloredCaption));
        expected.setTextColor(Color.BLACK);
        expected.setTextSize((float) 18.0);
        Log.d("CommentViewTests", "caption = " + caption.getText());
        assertEquals(expected.getText().toString(), caption.getText().toString());
    }

    @Test
    public void testCommentCreateViewForUrlContentDefaultPageTitle(){
        Message message = createMessageForCommentView(Content.ContentType.URL);
        CommentView commentView = new CommentView(YieldsApplication.getApplicationContext(), message);
        View view = commentView.getChildAt(0);

        TextView title = (TextView) view.findViewById(R.id.title);
        assertEquals("9GAG - Go Fun Yourself", title.getText());
    }

    @Test
    public void testCommentCreateViewForUrlContentDefaultPageDescription(){
        Message message = createMessageForCommentView(Content.ContentType.URL);
        CommentView commentView = new CommentView(YieldsApplication.getApplicationContext(), message);
        View view = commentView.getChildAt(0);

        TextView dscr = (TextView) view.findViewById(R.id.description);
        assertEquals("9GAG has the best funny pics, GIFs, videos, memes, cute, wtf, geeky, cosplay photos on the web. " +
                "9GAG is your best source of fun. Check out 9GAG now!", dscr.getText());
    }

    private Message createMessageForCommentView(Content.ContentType type){
        Content content = null;
        switch (type){
            case TEXT:
                content = new TextContent("topkek");
                break;
            case IMAGE:
                content = new ImageContent(YieldsApplication.getDefaultUserImage(), "topkek");
                break;
            case URL:
                content = new UrlContent("topkek 9gag.com");
                SystemClock.sleep(1000);
                break;
        }
        mDate = new Date();
        User user = YieldsApplication.getUser();
        return new Message(new Id(117), user.getId(), content, mDate, Message.MessageStatus.NOT_SENT);
    }
}
