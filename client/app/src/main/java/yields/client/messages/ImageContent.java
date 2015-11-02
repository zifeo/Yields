package yields.client.messages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;

import yields.client.exceptions.ContentException;
import yields.client.exceptions.MessageException;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Content type used for images and an associated caption.
 */
public class ImageContent implements Content{

    private Bitmap mImage;
    private String mCaption;

    /**
     * Mains constructor, builds a Content from an image and a caption.
     * @param img The image this content contains.
     * @param caption The caption this content contains.
     * @throws ContentException If the image is null.
     */
    public ImageContent(Bitmap img, String caption) throws ContentException {
        Objects.requireNonNull(img);
        mImage = Bitmap.createBitmap(img);
        mCaption = new String(caption);
    }

    /**
     * Returns the caption of this Content.
     * @return The caption of this Content.
     */
    public String getCaption(){
        return new String(mCaption);
    }

    /**
     * Returns the image of this Content.
     * @return The image of this Content.
     */
    public Bitmap getImage(){
        return Bitmap.createBitmap(mImage);
    }

    /**
     * Returns a String which describes the type of the Content.
     * @return A String which describes the type of the Content - "image".
     */
    @Override
    public String getType() {
        return "image";
    }

    /**
     * Returns a View which displays the data this Content contains.
     * @return A View which displays the data this Content contains.
     * @throws ContentException If a View could not be built from the data of this Content.
     */
    @Override
    public View getView() throws ContentException{
        WindowManager wm = (WindowManager) YieldsApplication.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(YieldsApplication.getApplicationContext());
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView img = new ImageView(YieldsApplication.getApplicationContext());
        if (mImage.getWidth() > (screenWidth - 200)){ // TODO : remove hardcoded value
            float scalefactor = ((float)(screenWidth - 200)) / mImage.getWidth();
            img.setImageBitmap(Bitmap.createScaledBitmap(mImage, (screenWidth - 200), (int) (scalefactor * mImage.getHeight()), false));
        }
        else {
            img.setImageBitmap(mImage);
        }
        TextView caption = new TextView(YieldsApplication.getApplicationContext());
        caption.setText(mCaption);
        caption.setTextColor(Color.BLACK);
        caption.setTextSize(20.f);
        layout.addView(caption);
        layout.addView(img);
        return layout;
    }

    /**
     * Returns a preview of the content, displayed in the group list with the content's sender
     * @return a string describing the content
     */
    @Override
    public String getPreview() {
        return "image";
    }
}
