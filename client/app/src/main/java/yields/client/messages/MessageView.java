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
import yields.client.exceptions.ContentException;
import yields.client.exceptions.MessageViewException;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * A View which corresponds to a given message.
 */
public class MessageView extends LinearLayout{

    private Message mMessage;

    /**
     * Main constructor for MessageView, it constructs itself based on the information which
     * the Message parameter contains.
     * @param context The context of the Application.
     * @param message The Message from which the MessageView is built.
     * @throws MessageViewException If the message contains incorrect infromation.
     */
    public MessageView(Context context, Message message) throws MessageViewException {
        super(context);
        if (message == null){
            throw new MessageViewException("Error, null message in MessageView constructor");
        }
        this.mMessage = message;
        this.addView(createMessageView());
    }

    /**
     * Returns the Message from which this View was built.
     * @return The Message from which this View was built.
     */
    public Message getMessage(){
        return mMessage;
    }

    /**
     * Creates the view for this instance.
     * @return The View for this instance.
     * @throws MessageViewException If the message contains incorrect information.
     */
    private View createMessageView() throws MessageViewException {
        Context applicationContext = YieldsApplication.getApplicationContext();
        
        LayoutInflater vi;
        vi = LayoutInflater.from(applicationContext);
        View v = vi.inflate(R.layout.messagelayout, null);

        ImageView imageViewProfilPicture = (ImageView) v.findViewById(R.id.profilpic);
        //TODO: Retrieve actual user picture
        Bitmap image = mMessage.getSender().getImg();
        image = getCroppedCircleBitmap(image, 80);
        imageViewProfilPicture.setImageBitmap(image);

        LinearLayout userNameAndDateLayout = new LinearLayout(applicationContext);
        userNameAndDateLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout relativeLayout = new RelativeLayout(applicationContext);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        TextView username = new TextView(applicationContext);
        username.setText(mMessage.getSender().getName());
        username.setTextSize(10);
        username.setTextColor(Color.rgb(39, 89, 196));
        userNameAndDateLayout.addView(username);
        TextView date = new TextView(applicationContext);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(mMessage.getDate());
        date.setText(time);
        date.setTextSize(10);
        date.setAlpha(0.6f);
        date.setTextColor(Color.GRAY);
        relativeLayout.addView(date, lp);

        LinearLayout usernameDate = (LinearLayout) v.findViewById(R.id.usernamedate);
        userNameAndDateLayout.addView(relativeLayout);
        usernameDate.addView(userNameAndDateLayout);

        LinearLayout contentLayout = (LinearLayout) v.findViewById(R.id.contentfield);
        try {
            contentLayout.addView(mMessage.getContent().getView());
        } catch (ContentException e) {
            throw new MessageViewException("Error, couldn't create contentLayout in createMessageView()");
        }
        return v;
    }


    /**
     *  Computes a circle shaped {@code Bitmap} image of the one passed as an argument (must be a square).
     *  The input image must have a square shape.
     * @param inputImage The image that is corped in a circle shape manner.
     * @param diameter The diameter of the the new image.
     * @return  A {@code Bitmap} image which has a circle shape.
     */
    private static Bitmap getCroppedCircleBitmap(Bitmap inputImage, int diameter)
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
