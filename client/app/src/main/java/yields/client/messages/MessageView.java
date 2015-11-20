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
        String senderEmail = mMessage.getSender().getEmail();
        String currentUserEmail = YieldsApplication.getUser().getEmail();
        boolean userIsSender = senderEmail.equals(currentUserEmail);
        Context applicationContext = YieldsApplication.getApplicationContext();

        LayoutInflater vi;
        vi = LayoutInflater.from(applicationContext);
        View v;
        if (userIsSender) {
            v = vi.inflate(R.layout.messagelayoutsender, null);
        }
        else{
            v = vi.inflate(R.layout.messagelayoutnotsender, null);
        }

        ImageView imageViewProfilPicture;
            imageViewProfilPicture = (ImageView) v.findViewById(R.id.profilpic);

        Bitmap image = mMessage.getSender().getImg();
        image = GraphicTransforms.getCroppedCircleBitmap(image, 80);
        imageViewProfilPicture.setImageBitmap(image);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(mMessage.getDate());

        TextView nameTextView = (TextView) v.findViewById(R.id.nametextview);
        TextView dateTextView = (TextView) v.findViewById(R.id.datetextview);

        nameTextView.setText(mMessage.getSender().getName());
        nameTextView.setTextSize(10);
        nameTextView.setTextColor(Color.rgb(39, 89, 196));

        dateTextView.setText(time);
        dateTextView.setTextSize(10);
        dateTextView.setAlpha(0.6f);
        dateTextView.setTextColor(Color.GRAY);

        RelativeLayout contentLayout = (RelativeLayout) v.findViewById(R.id.contentfield);
        try {
            contentLayout.addView(mMessage.getContent().getView());
        } catch (ContentException e) {
            throw new MessageViewException("Error, couldn't create contentLayout in createMessageView()");
        }
        return v;
    }
}
