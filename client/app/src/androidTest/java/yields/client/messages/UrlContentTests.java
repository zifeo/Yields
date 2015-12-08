package yields.client.messages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import junit.framework.TestResult;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.net.URLStreamHandler;

import yields.client.R;
import yields.client.activities.MessageActivity;
import yields.client.exceptions.ContentException;
import yields.client.generalhelpers.MockModel;
import yields.client.yieldsapplication.YieldsApplication;

public class UrlContentTests extends ActivityInstrumentationTestCase2<MessageActivity> {
    private static final String CAPTION = "caption www.reddit.com www.4chan.org";

    public UrlContentTests(){
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
    }

    @Test
    public void testCaptionShouldContainAtLeastOneURL(){
        String caption = "topkek";
        try {
            UrlContent content = new UrlContent(caption);
            fail("Expecting exception.");
        }
        catch (ContentException e){
            // Success
        }
    }

    @Test
    public void testMakeUrlValid(){
        String[] urls = {"reddit.com", "www.4chan.org", "https://www.reddit.com", "https://rekt.net"};
        String[] expected = {
                "https://www.reddit.com",
                "https://www.4chan.org",
                "https://www.reddit.com",
                "https://www.rekt.net"
        };
        for (int i = 0 ; i < urls.length ; i ++){
            assertEquals(expected[i], UrlContent.makeUrlValid(urls[i]));
        }
    }

    @Test
    public void testExtractUrlFromCaption(){
        String captions[] = {
                "blablabla : www.kek.net",
                "get rekt.ru",
                "topkek https://www.facebook.com",
                "qwdiqwdoin www.dankestmemes.net qweqwqwfqwfqwfqwf"
        };

        String expected[] = {
                "www.kek.net",
                "rekt.ru",
                "https://www.facebook.com",
                "www.dankestmemes.net"
        };
        for (int i = 0 ; i < captions.length ; i ++){
            assertEquals(expected[i], UrlContent.extractUrlFromCaption(captions[i]));
        }
    }

    @Test
    public void testContentShouldBeURL(){
        UrlContent content = createUrlContent();
        assertEquals(Content.ContentType.URL, content.getType());
    }

    @Test
    public void testGetColoredCaption(){
        UrlContent content = createUrlContent();
        assertEquals("caption <font color='#00BFFF'>www.reddit.com</font> www.4chan.org", content.getColoredCaption());
    }

    @Test
    public void testGetUrl(){
        UrlContent content = createUrlContent();
        assertEquals("https://www.reddit.com", content.getUrl());
    }

    @Test
    public void testGetDescription() {
        UrlContent content = createUrlContent();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.d("UrlContentTests", "Sleep interrupted in testGetDescription.");
        }
        assertEquals("reddit: the front page of the internet", content.getDescription());
    }

    @Test
    public void testGetTitle(){
        UrlContent content = createUrlContent();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.d("UrlContentTests", "Sleep interrupted in testGetTitle.");
        }
        assertEquals("reddit: the front page of the internet", content.getTitle());
    }

    @Test
    public void testGetPreview(){
        UrlContent content = createUrlContent();
        assertEquals(CAPTION, content.getPreview());
    }

    @Test
    public void testGetTextForRequest(){
        UrlContent content = createUrlContent();
        assertEquals(CAPTION, content.getTextForRequest());
    }

    @Test
    public void testContainsUrl(){
        String captions[] = {
                "tokekekekekekekeke",
                "qwioenqwoien.wwwfwf",
                "ttttttt.com",
                "qwe.net",
                "qwe.org",
                "qwe.fr",
                "qwrqgefegeg.ru",
                "NoUrlHere....qwe.qwr.qwr",
        };
        boolean[] expected = {
                false,
                true,
                true,
                true,
                true,
                true,
                true,
                false
        };
        for (int i = 0 ; i < captions.length ; i ++){
            assertEquals("caption " + i, expected[i], UrlContent.containsUrl(captions[i]));
        }
    }

    @Test
    public void testContentShouldBeCommentable(){
        UrlContent content = createUrlContent();
        assertTrue(content.isCommentable());
    }

    private static UrlContent createUrlContent(){
        return new UrlContent(CAPTION);
    }
}
