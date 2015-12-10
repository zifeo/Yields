package yields.client.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
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
import yields.client.activities.UserInfoActivity;
import yields.client.exceptions.ContentException;
import yields.client.exceptions.MessageViewException;
import yields.client.gui.GraphicTransforms;
import yields.client.node.Node;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * A View which corresponds to a given message.
 */
public class MessageView extends LinearLayout {

    private Message mMessage;

    /**
     * Main constructor for MessageView, it constructs itself based on the information which
     * the Message parameter contains.
     *
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
     * @throws MessageViewException If the message contains incorrect information.
     */
    private View createMessageView() throws MessageViewException {
        final Node sender = YieldsApplication.getNodeFromId(mMessage.getSender());
        boolean userIsSender = mMessage.getSender().equals(YieldsApplication.getUser().getId());
        final Context applicationContext = YieldsApplication.getApplicationContext();

        LayoutInflater vi;
        vi = LayoutInflater.from(applicationContext);
        View v;
        if (userIsSender) {
            v = vi.inflate(R.layout.messagelayoutsender, null);
        } else {
            v = vi.inflate(R.layout.messagelayoutnotsender, null);
        }

        ImageView imageViewProfilPicture;
        imageViewProfilPicture = (ImageView) v.findViewById(R.id.profilpic);
        imageViewProfilPicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                YieldsApplication.setUserSearched(sender);

                Intent intent = new Intent(applicationContext, UserInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(intent);
            }
        });


        Bitmap image = sender.getImage();
        image = GraphicTransforms.getCroppedCircleBitmap(image, 80);
        imageViewProfilPicture.setImageBitmap(image);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(mMessage.getDate());

        TextView nameTextView = (TextView) v.findViewById(R.id.nametextview);
        TextView dateTextView = (TextView) v.findViewById(R.id.datetextview);

        nameTextView.setText(sender.getName());
        nameTextView.setTextSize(10);
        nameTextView.setTextColor(Color.rgb(39, 89, 196));

        dateTextView.setText(time);
        dateTextView.setTextSize(10);
        dateTextView.setAlpha(0.6f);
        dateTextView.setTextColor(Color.GRAY);

        ImageView sentIndicator = (ImageView) v.findViewById(R.id.sentindicator);
        Bitmap sentImage;

        if (userIsSender) {
            switch (mMessage.getStatus()) {
                case SEEN:
                    sentImage = BitmapFactory.decodeResource(YieldsApplication.getResources(),
                            R.drawable.ic_check_circle_black_24dp);
                    sentIndicator.setImageBitmap(sentImage);
                    break;
                case SENT:
                    sentImage = BitmapFactory.decodeResource(YieldsApplication.getResources(),
                            R.drawable.ic_check_circle_black_24dp);
                    sentIndicator.setImageBitmap(sentImage);
                    break;
                case NOT_SENT:
                    sentImage = BitmapFactory.decodeResource(YieldsApplication.getResources(),
                            R.drawable.ic_query_builder_black_24dp);
                    sentIndicator.setImageBitmap(sentImage);
                    break;
                default:
                    sentImage = BitmapFactory.decodeResource(YieldsApplication.getResources(),
                            R.drawable.ic_query_builder_black_24dp);
                    sentIndicator.setImageBitmap(sentImage);
                    break;
            }
        }

        RelativeLayout contentLayout = (RelativeLayout) v.findViewById(R.id.contentfield);
        try {
            contentLayout.addView(mMessage.getContent().getView());
        } catch (ContentException e) {
            throw new MessageViewException("Error, couldn't create contentLayout in createMessageView()");
        }
        return v;
    }
}
