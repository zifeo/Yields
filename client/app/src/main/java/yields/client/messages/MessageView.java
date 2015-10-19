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
import android.view.LayoutInflater;
import android.view.View;
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

    private Message mMessage;

    public MessageView(Context context, Message message) {
        super(context);
        this.mMessage = message;
        this.addView(createMessageView(message));
    }

    public Message getMessage(){
        return mMessage;
    }

    private View createMessageView(Message message) {
        Context applicationContext = YieldsApplication.getApplicationContext();
        
        LayoutInflater vi;
        vi = LayoutInflater.from(applicationContext);
        View v = vi.inflate(R.layout.messagelayout, null);

        ImageView imageViewProfilPicture = (ImageView) v.findViewById(R.id.profilpic);
        //TODO: Retrieve actual user picture
        Bitmap image = BitmapFactory.decodeResource(YieldsApplication.getResources(),
                R.drawable.userpicture);
        image = getCroppedCircleBitmap(image, 80);
        imageViewProfilPicture.setImageBitmap(image);

        LinearLayout userNameAndDateLayout = new LinearLayout(applicationContext);
        userNameAndDateLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout relativeLayout = new RelativeLayout(applicationContext);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        TextView username = new TextView(applicationContext);
        username.setText("Nicolas");
        username.setTextSize(10);
        username.setTextColor(Color.rgb(39, 89, 196));
        userNameAndDateLayout.addView(username);
        TextView date = new TextView(applicationContext);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(message.getDate());
        date.setText(time);
        date.setTextSize(10);
        date.setAlpha(0.6f);
        date.setTextColor(Color.GRAY);
        relativeLayout.addView(date, lp);

        LinearLayout usernameDate = (LinearLayout) v.findViewById(R.id.usernamedate);
        userNameAndDateLayout.addView(relativeLayout);
        usernameDate.addView(userNameAndDateLayout);

        LinearLayout contentLayout = (LinearLayout) v.findViewById(R.id.contentfield);
        contentLayout.addView(message.getContent().getView());
        return v;
    }


    /**
     *  Computes a circle shaped {@code Bitmap} image of the one passed as an argument.
     *  The input image must have a square shape.
     * @param inputImage The image that is corped in a circle shape manner.
     * @param diameter The diameter of the the new image.
     * @return  A {@code Bitmap} image which has a circle shape.
     * @throws IllegalArgumentException If the inputImage does not have the same width as height.
     */
    private static Bitmap getCroppedCircleBitmap(Bitmap inputImage, int diameter)
            throws  IllegalArgumentException
    {
        if (inputImage.getWidth() != inputImage.getHeight()) {
            throw new IllegalArgumentException();
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
