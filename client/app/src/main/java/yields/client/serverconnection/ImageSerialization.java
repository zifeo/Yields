package yields.client.serverconnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageSerialization {

    public static final int SIZE_IMAGE = 800;
    public static final int SIZE_IMAGE_NODE = 200;

    public static String serializeImage(Bitmap image, int resizeTo) {

        if (image.getWidth() > resizeTo || image.getHeight() > resizeTo) {
            int width = image.getWidth();
            int height = image.getHeight();
            double ratio = width > height ? ((double) resizeTo) / width : ((double) resizeTo) / height;

            image = Bitmap.createScaledBitmap(image, (int) (width * ratio),
                    (int) (height * ratio), true);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 20, stream);

        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap unSerializeImage(String serialImage) {

        byte[] byteArray = Base64.decode(serialImage, Base64.DEFAULT);
        Bitmap img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        return img;
    }
}
