package yields.client.messages;

import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import yields.client.yieldsapplication.YieldsApplication;

public class TextContent implements Content {

    private String mText;

    public TextContent(String text){
        //TODO Check for safe content
        mText = text;
    }

    public String getText(){return mText;};

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public LinearLayout getLayout() {
        LinearLayout layout = new LinearLayout(YieldsApplication.getApplicationContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        TextView text = new TextView(YieldsApplication.getApplicationContext());
        text.setText(mText);
        text.setTextColor(Color.BLACK);
        text.setTextSize(20);
        layout.addView(text);
        return layout;
    }
}