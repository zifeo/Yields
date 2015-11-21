package yields.client.messages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import yields.client.R;
import yields.client.exceptions.ContentException;
import yields.client.exceptions.MessageViewException;
import yields.client.gui.GraphicTransforms;
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
        Objects.requireNonNull(message);
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
        image = GraphicTransforms.getCroppedCircleBitmap(image, 80);
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
}
