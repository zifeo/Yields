package yields.client.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import yields.client.R;
import yields.client.fragments.SaveImageToGalleryFragment;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Class used to display an full image.
 */
public class ImageShowPopUp extends Activity {

    private FragmentManager mFragmentManager;

    /**
     * Overriden method called on creation.
     * Set the image to be displayed on the screen.
     * @param savedInstanceState The bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show_pop_up);

        Bitmap image = YieldsApplication.getShownImage();

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
        mFragmentManager = this.getFragmentManager();

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SaveImageToGalleryFragment fragment = new SaveImageToGalleryFragment();
                fragment.show(mFragmentManager, "Save Image to Gallery");
                return true;
            }
        });
    }


}
