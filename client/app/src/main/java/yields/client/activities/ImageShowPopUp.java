package yields.client.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import yields.client.R;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Class used to display an full image.
 */
public class ImageShowPopUp extends Activity {

    /**
     * Overriden method called on creation.
     * Set the image to be displayed on the screen.
     * @param savedInstanceState The bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show_pop_up);

        Bitmap image = YieldsApplication.getShowImage();

        WindowManager wm = (WindowManager) YieldsApplication.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        float scalefactor = ((float) (screenWidth)) / image.getWidth();
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, screenWidth, (int)
                (scalefactor * image.getHeight()), false);

        ImageView imageView = (ImageView) findViewById(R.id.popupimage);
        imageView.setImageBitmap(resizedImage);
    }
}
