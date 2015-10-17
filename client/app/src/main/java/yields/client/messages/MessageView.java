package yields.client.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MessageView extends LinearLayout{

    public MessageView(Context context, Message m) {
        super(context);
    }

    public MessageView(Context context, AttributeSet attrs, Message m) {
        super(context, attrs);
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr, Message m) {
        super(context, attrs, defStyleAttr);
    }

    private void createMessageView(Message m){
        // TODO : add elems from the message in 'this'.
    }
}
