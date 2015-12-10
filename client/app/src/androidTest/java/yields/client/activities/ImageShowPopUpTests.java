package yields.client.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import yields.client.messages.ImageContent;
import yields.client.messages.Message;
import yields.client.messages.MessageView;
import yields.client.messages.TextContent;
import yields.client.node.ClientUser;
import yields.client.node.Group;
import yields.client.yieldsapplication.YieldsApplication;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

/**
 * Tests which test the MessageActivity (display and interaction).
 */
@RunWith(AndroidJUnit4.class)
public class ImageShowPopUpTests extends ActivityInstrumentationTestCase2<ImageShowPopUp> {

    private static Bitmap mockImage;

    public ImageShowPopUpTests() {
        super(ImageShowPopUp.class);

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

        mockImage = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
        YieldsApplication.setShownImage(mockImage);
    }

    @Test
    public void testImageIsShownAndResizedProperly(){
        ImageShowPopUp activity = getActivity();
        WindowManager wm = (WindowManager) YieldsApplication.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        float scalefactor = ((float) (screenWidth)) / mockImage.getWidth();
        Bitmap expectedImage = Bitmap.createScaledBitmap(mockImage, screenWidth, (int)
                (scalefactor * mockImage.getHeight()), false);
        ImageView imageView = (ImageView) activity.findViewById(R.id.popupimage);

        Bitmap activityImage = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        assertTrue(expectedImage.sameAs(activityImage));
    }
}