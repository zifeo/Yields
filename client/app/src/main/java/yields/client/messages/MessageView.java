package yields.client.messages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import yields.client.R;
import yields.client.yieldsapplication.YieldsApplication;

public class MessageView extends LinearLayout{

    private static final int BACKGROUND_COLORS[] = {Color.WHITE, Color.GRAY};
    private static int sColorIndex = 0;

    public MessageView(Context context, Message m, boolean showUsername) throws IOException {
        super(context);
        createMessageView(m, showUsername);
    }

    public MessageView(Context context, AttributeSet attrs, Message m, boolean showUsername) {
        super(context, attrs);
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr, Message m, boolean showUsername) {
        super(context, attrs, defStyleAttr);
    }

    private void createMessageView(Message message, boolean showUsername) throws IOException {
        int messageColor = BACKGROUND_COLORS[sColorIndex];
        sColorIndex = (sColorIndex + 1) % 2;
        //this.setBackgroundColor(messageColor);

        //tmp
        ImageView imageView = new ImageView(YieldsApplication.getApplicationContext());
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.userpicture);
        image = getCroppedBitmap(image,80);

        imageView.setImageBitmap(image);

        //this.setBackgroundColor(messageColor);
        this.setOrientation(HORIZONTAL);
        this.addView(imageView);

        LinearLayout userNameAndDateLayout = new LinearLayout(YieldsApplication.getApplicationContext());
        userNameAndDateLayout.setOrientation(HORIZONTAL);
        RelativeLayout relativeLayout = new RelativeLayout(YieldsApplication.getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
                (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


        if (showUsername){
            TextView username  = new TextView(YieldsApplication.getApplicationContext());
            username.setText("Nicolas");
            username.setTextSize(10);
            username.setTextColor(Color.rgb(39, 89, 196));
            userNameAndDateLayout.addView(username);
        }
        TextView date = new TextView(YieldsApplication.getApplicationContext());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time  = dateFormat.format(message.getDate());
        date.setText(time);
        date.setTextSize(10);
        date.setAlpha(0.6f);
        date.setTextColor(Color.GRAY);
        relativeLayout.addView(date, lp);

        userNameAndDateLayout.addView(relativeLayout);
        LinearLayout contentLayout = new LinearLayout(YieldsApplication.getApplicationContext());
        contentLayout.setOrientation(VERTICAL);
        contentLayout.addView(userNameAndDateLayout);

        LinearLayout content = message.getContent().getLayout();
        contentLayout.addView(content);

        this.setOrientation(HORIZONTAL);
        this.addView(contentLayout);

        /*GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(3, Color.GRAY);
        drawable.setAlpha(60);
        this.setBackground(drawable);*/
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
                sbmp.getWidth() / 2+0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }
}
