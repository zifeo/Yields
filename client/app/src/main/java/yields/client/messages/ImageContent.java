package yields.client.messages;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import yields.client.yieldsapplication.YieldsApplication;

public class ImageContent implements Content{
    private Bitmap mImage;
    private String mCaption;

    public ImageContent(Bitmap img, String caption){
        mImage = Bitmap.createBitmap(img);
        mCaption = new String(caption);
    }

    @Override
    public String getType() {
        return "image";
    }

    @Override
    public View getView() {
        LinearLayout layout = new LinearLayout(YieldsApplication.getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView img = new ImageView(YieldsApplication.getApplicationContext());
        img.setImageBitmap(mImage);
        TextView caption = new TextView(YieldsApplication.getApplicationContext());
        caption.setText(mCaption);
        caption.setTextColor(Color.BLACK);
        caption.setTextSize(20.f);
        layout.addView(caption);
        layout.addView(img);
        return img;
    }
}
