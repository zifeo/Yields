package yields.client.messages;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import yields.client.exceptions.ContentException;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Content representing an Url in a message.
 */
public class UrlContent extends Content {
    private String mCaption;
    private String mUrl;
    private String mTitle;
    private String mDescription;

    private static final String URL_REGEX = "(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    private static final String URL_COLOR = "#00BFFF";

    private static final String ERROR_DESCRIPTION_INVALID_URL = "Unknown page";
    private static final String NO_DESCRIPTION = "No description";
    private static final String NO_TITLE = "No title";
    private static final String CONNECTION_ERROR = "No connection";

    /**
     * Constructor of a Url content for the given caption, the url is directely extracted from
     * the text. If there are multiple urls, only the first one is taken into account.
     *
     * @param caption The caption of the message.
     */
    public UrlContent(String caption) {
        mCaption = Objects.requireNonNull(caption);
        mUrl = extractUrlsFromCaption(caption);
        if (mUrl.length() == 0) {
            throw new ContentException("Error in URL content constructor, caption does not contain any URL.");
        }
        makeUrlValid(mUrl);
        Log.d("UrlContent", "Caption : " + caption + "  contains url : " + mUrl);
        getTitleAndDescrition();
        Log.d("UrlContent", "Title = " + mTitle);
        Log.d("UrlContent", "Description = " + mDescription);
    }

    /**
     * Return the content type.
     *
     * @return The content type.
     */
    @Override
    public ContentType getType() {
        return ContentType.URL;
    }

    /**
     * Construct the View of the content, ie : How it will be represented in a MessageView.
     *
     * @return The View containing the content.
     * @throws ContentException If the view cannot be created.
     */
    @Override
    public View getView() throws ContentException {
        TextView text = new TextView(YieldsApplication.getApplicationContext());
        String viewText = getColoredCaption();
        text.setText(Html.fromHtml(viewText));
        text.setTextSize(20);
        text.setTextColor(Color.BLACK);
        return text;
    }

    public String getColoredCaption() {
        String coloredCaption = mCaption;
        coloredCaption = coloredCaption.replace(mUrl, "<font color='#00BFFF'>" + mUrl + "</font>");
        return coloredCaption;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getTitle() {
        return mTitle;
    }

    public static String makeUrlValid(String url){
        boolean addHttp = false;
        boolean addWww = false;
        if (!url.startsWith("http://")){
            addHttp = true;
        }
        if (!url.contains("www.")){
            addWww = true;
        }

        if (addHttp){
            if (addWww) {
                url = "http://www." + url;
            }
            else{
                url = "http://" + url;
            }
        }
        else if (addWww){
            url = "http://" + url.substring(7, url.length());
        }
        return url;
    }

    /**
     * Return the preview of the content.
     *
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
        return mCaption;
    }

    @Override
    public boolean isCommentable() {
        return true;
    }

    @Override
    public String getContentForRequest() {
        return mUrl;
    }

    /**
     * Check if the text given in parameter contains an URL.
     *
     * @param text The text to check.
     * @return True if the text indeed contains an URL, False otherwise.
     */
    public static boolean containsUrl(String text) {
        return extractUrlsFromCaption(text).length() != 0;
    }

    /**
     * Extract all the urls from a caption.
     *
     * @param caption The caption.
     * @return An array list containing the URLs in order.
     */
    private static String extractUrlsFromCaption(String caption) {
        Log.d("UrlContent", "extractUrlsFromCaption : " + caption);
        String words[] = caption.split(" ");
        for (String word : words) {
            Log.d("UrlContent", "Word : " + word);
            if (word.contains(".")) {
                Matcher matcher = URL_PATTERN.matcher(word);
                if (matcher.matches()) {
                    return word;
                }
            }
        }
        return "";
    }

    private String inputStreamToString(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();

        try {
            String line = reader.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private void getTitleAndDescrition() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                URL url = null;
                try {
                    url = new URL(mUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.connect();
                    String pageBody = inputStreamToString(connection.getInputStream());
                    mTitle = getTitleFromMetadata(pageBody);
                    mDescription = getDescriptionFromMetadata(pageBody);
                } catch (IOException e) {
                    Log.d("UrlContent", "Cannot get page infos.");
                    mTitle = CONNECTION_ERROR;
                    mDescription = CONNECTION_ERROR;
                }
                return null;
            }
        };
        task.execute();
    }

    private static String getTitleFromMetadata(String pageBody) {
        int posTitleOpen = pageBody.indexOf("<title>");
        int lengthTitleField = new String("<title>").length();
        int posTitleClose = pageBody.indexOf("</title>");
        if (posTitleClose == -1 || posTitleOpen == -1) {
            // cannot find title
            return NO_TITLE;
        }
        String title = pageBody.substring(posTitleOpen + lengthTitleField, posTitleClose);
        return title;
    }

    private static String getDescriptionFromMetadata(String pageBody) {
        int posMetaDescr = pageBody.indexOf("meta name=\"description\" content=\"");
        int lengthMetaDescrField = new String("meta name=\"description\" content=\"").length();
        if (posMetaDescr == -1) {
            // No description.
            return NO_DESCRIPTION;
        }
        StringBuilder builder = new StringBuilder();
        boolean found = false;
        for (int i = posMetaDescr + lengthMetaDescrField; i < pageBody.length(); i++) {
            if (pageBody.charAt(i) != '"') {
                builder.append(pageBody.charAt(i));
            } else {
                found = true;
            }
        }
        if (!found) {
            return NO_DESCRIPTION;
        } else {
            return builder.toString();
        }
    }
}
