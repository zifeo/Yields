package yields.client.messages;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import yields.client.yieldsapplication.YieldsApplication;

public class ImageContent implements Content{
    private Bitmap mImage;

    public ImageContent(Bitmap img){
        mImage = Bitmap.createBitmap(img);
    }

    @Override
    public String getType() {
        return "image";
    }

    @Override
    public View getView() {
        ImageView img = new ImageView(YieldsApplication.getApplicationContext());
        img.setImageBitmap(mImage);
        return img;
    }
}
