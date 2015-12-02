package yields.client.messages;

import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

import yields.client.exceptions.ContentException;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Content representing an Url in a message.
 */
public class UrlContent extends Content{
    private static String mCaption;
    private static ArrayList<String> mUrl;

    /**
     * Constructor of a Url content for the given caption, the url is directely extracted from
     * the text. If there are multiple urls, only the first one is taken into account.
     * @param caption The caption of the message.
     */
    public UrlContent(String caption){
        mCaption = Objects.requireNonNull(caption);
        ArrayList<String> urls  = extractUrlsFromCaption(caption);
        if (urls.size() == 0){
            throw new ContentException("Error : Trying to construct an UrlContent without URL.");
        }
        else{
            mUrl = urls;
        }
    }

    /**
     * Return the content type.
     * @return The content type.
     */
    @Override
    public ContentType getType() {
        return ContentType.URL;
    }

    /**
     * Construct the View of the content, ie : How it will be represented in a MessageView.
     * @return The View containing the content.
     * @throws ContentException If the view cannot be created.
     */
    @Override
    public View getView() throws ContentException {
        TextView text = new TextView(YieldsApplication.getApplicationContext());
        String viewText = mCaption;
        for (int i = 0 ; i < mUrl.size() ; i ++){
            viewText = viewText.replace(mUrl.get(i), "<font color='#EE0000'>" + mUrl.get(i) + "</font>");
        }
        text.setText(Html.fromHtml(viewText));
        text.setTextSize(20);
        return text;
    }

    /**
     * Return the preview of the content.
     * @return The URL in String format.
     */
    @Override
    public String getPreview() {
        return mCaption;
    }

    /**
     * Returns the text of the Content, this should exclusively be used for requests.
     *
     * @return The text associated to this Content (for a request).
     */
    @Override
    public String getTextForRequest() {
        return null;
    }

    /**
     * Check if the text given in parameter contains an URL.
     * @param text The text to check.
     * @return True if the text indeed contains an URL, False otherwise.
     */
    public static boolean containsUrl(String text){
        return extractUrlsFromCaption(text).size() != 0;
    }

    /**
     * Extract all the urls from a caption.
     * @param caption The caption.
     * @return An array list containing the URLs in order.
     */
    private static ArrayList<String> extractUrlsFromCaption(String caption){
        String words[] = caption.split(" ");
        ArrayList<String> urls = new ArrayList<>();
        for (String word : words) {
            if (word.contains(".")) {
                try {
                    URL url = new URL(word);
                    urls.add(word);
                } catch (MalformedURLException e) {
                    // nothing.
                }
            }
        }
        return urls;
    }
}
