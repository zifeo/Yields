package yields.client.messages;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import yields.client.yieldsapplication.YieldsApplication;

public class TextContent implements Content {

    private String mText;

    public TextContent(String text){
        //TODO Check for safe content
        mText = new String(text);
    }

    public String getText(){return new String(mText);};

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public View getView() {
        TextView text = new TextView(YieldsApplication.getApplicationContext());
        text.setText(mText);
        text.setTextColor(Color.BLACK);
        text.setTextSize(20);
        return text;
    }
}