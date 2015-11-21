package yields.client.messages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import yields.client.R;
import yields.client.exceptions.CommentViewException;
import yields.client.exceptions.ContentException;
import yields.client.exceptions.MessageViewException;
import yields.client.gui.GraphicTransforms;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * A View which corresponds to a message commented in the comment fragment.
 */
public class CommentView extends LinearLayout {

    private Message mMessage;

    /**
     * Main constructor for MessageView, it constructs itself based on the information which
     * the Message parameter contains.
     * @param context The context of the Application.
     * @param message The Message from which the MessageView is built.
     * @throws MessageViewException If the message contains incorrect infromation.
     */
    public CommentView(Context context, Message message) throws MessageViewException {
        super(context);
        Objects.requireNonNull(message);
        if (message.getContent().getType() == Content.ContentType.TEXT){
            throw new CommentViewException("Error : Cannot create comment view for a text message.");
        }
        this.mMessage = message;
        this.addView(createCommentView());
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
     */
    private View createCommentView() {
        Context applicationContext = YieldsApplication.getApplicationContext();

        LayoutInflater vi;
        vi = LayoutInflater.from(applicationContext);
        View v = vi.inflate(R.layout.commentlayout, null);

        RelativeLayout rawcontent = (RelativeLayout) v.findViewById(R.id.rawcontent);
        TextView caption = (TextView) v.findViewById(R.id.caption);
        TextView messageInfos = (TextView) v.findViewById(R.id.messageinfos);

        switch (mMessage.getContent().getType()){
            case IMAGE:
                ImageContent content = (ImageContent) mMessage.getContent();
                ImageView image = new ImageView(YieldsApplication.getApplicationContext());
                image.setImageBitmap(content.getImage());
                rawcontent.addView(image);

                caption.setTextSize((float) 12.0);
                caption.setText(content.getCaption());
                caption.setTextColor(Color.BLACK);

                messageInfos.setText("Sent by " + mMessage.getSender().getName() + " at " +
                        mMessage.getDate().toString());
                messageInfos.setTextColor(Color.BLACK);
                messageInfos.setTextSize((float) 10.0);
                break;

            default:
                throw new CommentViewException("Error message type not supported.");
        }

        return v;
    }
}