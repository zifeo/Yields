package yields.client.messages;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import yields.client.yieldsapplication.YieldsApplication;

/**
 * A Content used for Messages which are only made of text.
 */
public class TextContent extends Content {

    private String mText;

    /**
     * Constructs a TextContent for a given String.
     *
     * @param text The String for which the TextContent is built.
     */
    public TextContent(String text) {
        super();
        //TODO Check for safe content
        mText = new String(text);
    }

    /**
     * Returns the text associated to this Content.
     *
     * @return The text associated to this Content.
     */
    public String getText() {
        return mText;
    }

    /**
     * Returns a String which describes the Ttype of this Content.
     *
     * @return A string which describes the type of this Content - "text".
     */
    @Override
    public ContentType getType() {
        return ContentType.TEXT;
    }

    /**
     * Returns a View which corresponds to text of this instance.
     */
    @Override
    public View getView() {
        TextView text = new TextView(YieldsApplication.getApplicationContext());
        text.setText(mText);
        text.setTextColor(Color.BLACK);
        text.setTextSize(20);
        return text;
    }

    /**
     * Returns a preview of the text content, displayed in the group list with the content's sender
     *
     * @return a string corresponding to the content
     */
    @Override
    public String getPreview() {
        return mText;
    }

    /**
     * Returns the text of the Content, this should exclusively be used for requests.
     *
     * @return The text associated to this Content (for a request).
     */
    @Override
    public String getTextForRequest() {
        return mText;
    }

    @Override
    public String getContentForRequest() {
        return null;
    }
}