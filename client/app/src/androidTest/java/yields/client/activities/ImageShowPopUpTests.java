package yields.client.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import yields.client.R;
import yields.client.generalhelpers.ServiceTestConnection;
import yields.client.yieldsapplication.YieldsApplication;
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