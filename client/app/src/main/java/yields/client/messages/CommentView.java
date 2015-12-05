package yields.client.messages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.ColorRes;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import yields.client.R;
import yields.client.exceptions.CommentViewException;
import yields.client.exceptions.MessageViewException;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * A View which corresponds to a message commented in the comment fragment.
 */
public class CommentView extends LinearLayout {

    private Message mMessage;

    /**
     * Main constructor for MessageView, it constructs itself based on the information which
     * the Message parameter contains.
     *
     * @param context The context of the Application.
     * @param message The Message from which the MessageView is built.
     * @throws MessageViewException If the message contains incorrect infromation.
     */
    public CommentView(Context context, Message message) throws MessageViewException {
        super(context);
        Objects.requireNonNull(message);
        if (message.getContent().getType() == Content.ContentType.TEXT) {
            throw new CommentViewException("Error : Cannot create comment view for a text message.");
        }
        this.mMessage = message;
        this.addView(createCommentView());
    }

    /**
     * Returns the Message from which this View was built.
     *
     * @return The Message from which this View was built.
     */
    public Message getMessage() {
        return mMessage;
    }

    /**
     * Creates the view for this instance.
     *
     * @return The View for this instance.
     */
    private View createCommentView() {
        Context applicationContext = YieldsApplication.getApplicationContext();

        LayoutInflater vi;
        vi = LayoutInflater.from(applicationContext);
        View v;
        TextView caption;
        TextView messageInfos;
        DateFormat dateFormat;
        String time;
        switch (mMessage.getContent().getType()) {
            case IMAGE:
                v = vi.inflate(R.layout.imagecommentlayout, null);

                ImageView imageView = (ImageView) v.findViewById(R.id.imageContent);
                caption = (TextView) v.findViewById(R.id.caption);
                messageInfos = (TextView) v.findViewById(R.id.messageinfos);

                ImageContent content = (ImageContent) mMessage.getContent();

                Bitmap image = content.getImage();
                WindowManager wm = (WindowManager) YieldsApplication.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screenWidth = size.x;
                float scalefactor = ((float) (screenWidth)) / image.getWidth();
                Bitmap resizedImage = Bitmap.createScaledBitmap(image, screenWidth, (int)
                        (scalefactor * image.getHeight()), false);

                int maxHeight = (int) ((1.f / 4) * size.y);
                if (maxHeight > resizedImage.getHeight()) {
                    maxHeight = resizedImage.getHeight();
                }
                resizedImage = Bitmap.createBitmap(resizedImage, 0, 0, resizedImage.getWidth(),
                        maxHeight);

                imageView.setImageBitmap(resizedImage);


                caption.setTextSize((float) 12.0);
                caption.setText(content.getCaption());
                caption.setTextColor(Color.BLACK);

                dateFormat = new SimpleDateFormat("HH:mm");
                time = dateFormat.format(mMessage.getDate());

                messageInfos.setText("Sent by " + YieldsApplication.getUser(
                        mMessage.getSender()).getName() + " at " +
                        time);
                messageInfos.setTextColor(Color.BLUE);
                messageInfos.setTextSize((float) 10.0);
                break;

            case URL:
                Log.d("CommentView", "Comment view for URL content.");
                v = vi.inflate(R.layout.urlcommentlayout, null);
                UrlContent urlContent = ((UrlContent) mMessage.getContent());

                caption = (TextView) v.findViewById(R.id.caption);
                messageInfos = (TextView) v.findViewById(R.id.messageinfos);

                caption.setText(Html.fromHtml(urlContent.getColoredCaption()));
                caption.setTextColor(Color.BLACK);
                caption.setTextSize((float) 18.0);
                dateFormat = new SimpleDateFormat("HH:mm");
                time = dateFormat.format(mMessage.getDate());

                messageInfos.setText("Sent by " + YieldsApplication.getUser(
                        mMessage.getSender()).getName() + " at " +
                        time);
                messageInfos.setTextColor(Color.BLUE);
                messageInfos.setTextSize((float) 10.0);

                break;

            default:
                throw new CommentViewException("Error message type not supported.");
        }

        return v;
    }
}