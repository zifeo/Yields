package yields.client.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import java.util.Objects;

/**
 * Class which contains static methods to transform graphical elements
 */
public class GraphicTransforms {

    /**
     * Class cannot be instantiated
     */
    private GraphicTransforms(){
    }

    /**
     *  Computes a circle shaped {@code Bitmap} image of the one passed as an argument (must be a square).
     *  The input image must have a square shape.
     * @param inputImage The image that is corped in a circle shape manner.
     * @param diameter The diameter of the the new image.
     * @return  A {@code Bitmap} image which has a circle shape.
     */
    public static Bitmap getCroppedCircleBitmap(Bitmap inputImage, int diameter){
        Objects.requireNonNull(inputImage);
        if (inputImage.getWidth() != inputImage.getHeight()) {
            throw new IllegalArgumentException("Image should be squared.");
        }

        Bitmap scaledInputImage;
        if (inputImage.getWidth() != diameter || inputImage.getHeight() != diameter) {
            scaledInputImage = Bitmap.createScaledBitmap(inputImage, diameter, diameter, false);
        } else {
            scaledInputImage = inputImage;
        }

        Bitmap outputImage = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Rect rect = new Rect(0, 0, scaledInputImage.getWidth(), scaledInputImage.getHeight());

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        Canvas canvas = new Canvas(outputImage);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledInputImage, rect, rect, paint);

        return outputImage;
    }
}
