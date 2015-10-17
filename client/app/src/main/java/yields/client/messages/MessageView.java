package yields.client.messages;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;


import yields.client.yieldsapplication.YieldsApplication;

public class MessageView extends LinearLayout{

    private static final int BACKGROUND_COLORS[] = {Color.WHITE, Color.GRAY};
    private static int sColorIndex = 0;

    public MessageView(Context context, Message m, boolean showUsername) {
        super(context);
        createMessageView(m, showUsername);
    }

    public MessageView(Context context, AttributeSet attrs, Message m, boolean showUsername) {
        super(context, attrs);
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr, Message m, boolean showUsername) {
        super(context, attrs, defStyleAttr);
    }

    private void createMessageView(Message m, boolean showUsername){
        int messageColor = BACKGROUND_COLORS[sColorIndex];
        sColorIndex = (sColorIndex + 1) % 2;
        this.setBackgroundColor(messageColor);


        LinearLayout userNameDate = new LinearLayout(YieldsApplication.getApplicationContext());
        userNameDate.setBackgroundColor(messageColor);
        userNameDate.setOrientation(HORIZONTAL);
        if (showUsername){
            TextView username  = new TextView(YieldsApplication.getApplicationContext());
            username.setText(m.getSender().getName());
            username.setTextSize(10);
            username.setTextColor(Color.BLACK);
            userNameDate.addView(username);
        }
        TextView date = new TextView(YieldsApplication.getApplicationContext());
        date.setText("mock date"); // TODO : add real date
        date.setTextSize(10);
        date.setTextColor(Color.BLACK);
        userNameDate.addView(date);

        LinearLayout content = m.getContent().getLayout();

        this.setOrientation(VERTICAL);
        this.addView(userNameDate);
        this.addView(content);
    }
}
