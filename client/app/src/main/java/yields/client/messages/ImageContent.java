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

import yields.client.exceptions.ContentException;
import yields.client.exceptions.MessageException;
import yields.client.yieldsapplication.YieldsApplication;

public class ImageContent implements Content{
    private Bitmap mImage;
    private String mCaption;

    public ImageContent(Bitmap img, String caption) throws ContentException {
        if (img == null){
            throw new ContentException("Error, passing null image to the ImageContent constructor.");
        }
        mImage = Bitmap.createBitmap(img);
        mCaption = new String(caption);
    }

    public String getCaption(){
        return new String(mCaption);
    }

    public Bitmap getImage(){
        return Bitmap.createBitmap(mImage);
    }

    @Override
    public String getType() {
        return "image";
    }

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
}
