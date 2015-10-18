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

import yields.client.yieldsapplication.YieldsApplication;

public class ImageContent implements Content{
    private Bitmap mImage;
    private String mCaption;

    public ImageContent(Bitmap img, String caption){
        if (img != null) {
            mImage = Bitmap.createBitmap(img);
        }
        mCaption = new String(caption);
    }

    @Override
    public String getType() {
        return "image";
    }

    @Override
    public View getView() {
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
        Log.d("DEBUG", "Width = " + mImage.getWidth());
        Log.d("DEBUG", "Height = " + mImage.getHeight());
        Log.d("DEBUG", "max Width = " + screenWidth);
        if (mImage.getWidth() > (screenWidth - 200)){ // TODO : remove hardcoded value
            float scalefactor = ((float)(screenWidth - 200)) / mImage.getWidth();
            Log.d("DEBUG", "Scale factor = " + scalefactor);
            Log.d("DEBUG", "New width = " +(screenWidth - 200));
            Log.d("DEBUG", "New height = " +(int) (scalefactor*mImage.getHeight()));
            img.setImageBitmap(Bitmap.createScaledBitmap(mImage, (screenWidth - 200), (int) (scalefactor*mImage.getHeight()), false));
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
}
