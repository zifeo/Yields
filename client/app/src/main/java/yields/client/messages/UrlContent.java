package yields.client.messages;

import android.view.View;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yields.client.exceptions.ContentException;

/**
 * Content representing an Url in a message.
 */
public class UrlContent extends Content{
    private static String mCaption;
    private static String mUrl;

    private static final String URL_REGEX = "(https?|ftp|file):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    /**
     * Constructor of a Url content for the given caption, the url is directely extracted from
     * the text. If there are multiple urls, only the first one is taken into account.
     * @param caption The caption of the message.
     */
    public UrlContent(String caption){
        mCaption = Objects.requireNonNull(caption);
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
        return null;
    }

    /**
     * Return the preview of the content.
     * @return The URL in String format.
     */
    @Override
    public String getPreview() {
        return mUrl;
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
        Matcher m = URL_PATTERN.matcher(Objects.requireNonNull(text));
        return m.matches();
    }
}
