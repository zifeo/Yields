package yields.client.messages;

import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yields.client.exceptions.ContentException;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Content representing an Url in a message.
 */
public class UrlContent extends Content{
    private static String mCaption;
    private static ArrayList<String> mUrl;

    private static final String URL_REGEX = "(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    private static final String URL_COLOR = "#00BFFF";

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
            Log.d("UrlContent", "Caption : " + caption + "  contains " + urls.size() +" URL.");
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
        String viewText = new String(mCaption);
        for (int i = 0 ; i < mUrl.size() ; i ++){
            viewText = viewText.replace(mUrl.get(i), "<font color='#00BFFF'>" + mUrl.get(i) + "</font>");
        }
        text.setText(Html.fromHtml(viewText));
        text.setTextSize(20);
        text.setTextColor(Color.BLACK);
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

    @Override
    public boolean isCommentable() {
        return true;
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
        Log.d("UrlContent", "extractUrlsFromCaption : " + caption);
        String words[] = caption.split(" ");
        ArrayList<String> urls = new ArrayList<>();
        for (String word : words) {
            Log.d("UrlContent", "Word : "  + word);
            if (word.contains(".")){
                Matcher matcher = URL_PATTERN.matcher(word);
                if (matcher.matches()){
                    urls.add(word);
                }
            }
        }
        return urls;
    }
}
