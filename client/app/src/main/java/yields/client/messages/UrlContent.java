package yields.client.messages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yields.client.exceptions.ContentException;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Content representing an Url in a message.
 */
public class UrlContent extends Content {
    private String mCaption;
    private String mUrl;
    private String mValidUrl;
    private String mTitle;
    private String mDescription;
    private Bitmap mThumbnail;

    private static final String URL_REGEX = "(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    private static final String ERROR_INVALID_URL = "Unknown page";
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
        mUrl = extractUrlFromCaption(caption);
        if (mUrl.length() == 0) {
            throw new ContentException("Error in URL content constructor, caption does not contain any URL.");
        }
        mValidUrl = makeUrlValid(mUrl);
        try {
            getTitleDescriptionAndThumbnail();
        } catch (ExecutionException | InterruptedException e) {
            mTitle = ERROR_INVALID_URL;
            mDescription = ERROR_INVALID_URL;
        }
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

    /**
     * Getter for the colored caption (link colored in blue).
     *
     * @return The caption in html format.
     */
    public String getColoredCaption() {
        String coloredCaption = mCaption;
        coloredCaption = coloredCaption.replace(mUrl, "<font color='#00BFFF'>" + mUrl + "</font>");
        return coloredCaption;
    }

    /**
     * Getter for the url contained in this content, that is the first URL foun in the caption.
     *
     * @return The original URL (not the valid format).
     */
    public String getUrl() {
        return mValidUrl;
    }

    /**
     * Getter for the description of the page having the current URL stored in this content.
     *
     * @return The description of the page comming from the metadata of the html body.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Getter for the title of the page having the current URL stored in this content.
     *
     * @return The Title of the page contained in the metadata of the html body.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Build a valid format for the URL passed in parameter, that is adding https:// and www. if needed.
     *
     * @param url The URL we want to convert into valid format.
     * @return The valid format of url.
     */
    public static String makeUrlValid(String url) {
        boolean addHttp = false;
        boolean addWww = false;
        if (!url.startsWith("https://")) {
            addHttp = true;
        }
        if (!url.contains("www.")) {
            addWww = true;
        }

        if (addHttp) {
            if (addWww) {
                url = "https://www." + url;
            } else {
                url = "https://" + url;
            }
        } else if (addWww) {
            url = "https://www." + url.substring(8, url.length());
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
        return mValidUrl;
    }

    /**
     * Check if the text given in parameter contains an URL.
     *
     * @param text The text to check.
     * @return True if the text indeed contains an URL, False otherwise.
     */
    public static boolean containsUrl(String text) {
        return extractUrlFromCaption(text).length() != 0;
    }

    /**
     * Extract all the urls from a caption.
     *
     * @param caption The caption.
     * @return An array list containing the URLs in order.
     */
    public static String extractUrlFromCaption(String caption) {
        String words[] = caption.split(" ");
        for (String word : words) {
            if (word.contains(".")) {
                Matcher matcher = URL_PATTERN.matcher(word);
                if (matcher.matches()) {
                    return word;
                }
            }
        }
        return "";
    }

    /**
     * Getter for the thumbnail of the web page.
     *
     * @return The thumbnail.
     */
    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    /**
     * Get the thumbnail from the page body.
     *
     * @param pageBody The HTML body of the page.
     * @return A thumbnail for the page. If no thumbnail can be found, returns the defaultThumbnail defined in the
     * YieldsApplication class.
     */
    public static Bitmap getThumbnailFromMetadata(String pageBody) {
        // 2 tests :
        // _ If there is a property="og:image" available we take it.
        // _ Else we take the first image that appears in the page body.

        int propertyPos = pageBody.indexOf("property=\"og:image\"");
        int imgBlockPos = pageBody.indexOf("<img");
        boolean found = false;
        String path = null;
        if (propertyPos != -1) {
            // There is the property in the body.
            int contentPos = pageBody.indexOf("content=\"", propertyPos);
            if (contentPos != -1) {
                int pathBegin = contentPos + 9;
                int pathEnd = pageBody.indexOf("\"", pathBegin);
                path = pageBody.substring(pathBegin, pathEnd);
                found = true;
            } else {
                // Need to take the first image.
                found = false;
            }
        }

        if (!found){
            if (imgBlockPos != -1) {
                int srcBegin = pageBody.indexOf("src=\"");
                int srcEnd = pageBody.indexOf("\"", srcBegin + 5);
                path = pageBody.substring(srcBegin + 5, srcEnd);
            } else {
                return YieldsApplication.getDefaultThumbnail();
            }
        }
        if (found){
            URL url = null;
            try {
                url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (MalformedURLException e) {
                return YieldsApplication.getDefaultThumbnail();
            } catch (IOException e) {
                return YieldsApplication.getDefaultThumbnail();
            }
        }
        else{
            return YieldsApplication.getDefaultThumbnail();
        }
    }

    /**
     * Convert an InputStream into a String format.
     *
     * @param stream The stream to convert.
     * @return The corresponding String.
     */
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
            return "";
        }

        return sb.toString();
    }

    /**
     * Extract the title and the description of the page from its html body.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void getTitleDescriptionAndThumbnail() throws ExecutionException, InterruptedException {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                URL url = null;
                try {
                    String pageBody;
                    boolean done = false;
                    HttpURLConnection connection;
                    url = new URL(mValidUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setInstanceFollowRedirects(true);
                    if (connection.getResponseCode() / 100 == 3) {
                        // Code 3XX mean redirect, we need to follow them.
                        String redirect = connection.getHeaderField("Location");
                        Log.d("UrlContent", "Redirect for the page " + mValidUrl + " => " + redirect);
                        mValidUrl = redirect;
                        URL redirectURL = new URL(redirect);
                        connection = (HttpURLConnection) redirectURL.openConnection();
                    }
                    pageBody = inputStreamToString(connection.getInputStream());
                    mTitle = getTitleFromMetadata(pageBody);
                    mDescription = getDescriptionFromMetadata(pageBody);
                    mThumbnail = getThumbnailFromMetadata(pageBody);
                } catch (MalformedURLException e) {
                    Log.d("UrlContent", "Cannot get page infos.");
                    mTitle = ERROR_INVALID_URL;
                    mDescription = ERROR_INVALID_URL;
                    mThumbnail = YieldsApplication.getDefaultThumbnail();
                } catch (IOException e) {
                    Log.d("UrlContent", "Cannot get page infos.");
                    mTitle = CONNECTION_ERROR;
                    mDescription = CONNECTION_ERROR;
                    mThumbnail = YieldsApplication.getDefaultThumbnail();
                }
                return null;
            }
        };
        task.execute();
    }

    /**
     * Extract the title of the page from its html body.
     *
     * @param pageBody The html body of the page.
     * @return The Title of the page.
     */
    public static String getTitleFromMetadata(String pageBody) {
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

    /**
     * Extract the description of the page from its html body.
     *
     * @param pageBody The html body of the page.
     * @return The description of the page.
     */
    public static String getDescriptionFromMetadata(String pageBody) {
        int posMetaDescr = pageBody.indexOf("meta name=\"description\" content=\"");
        int lengthMetaDescrField = new String("meta name=\"description\" content=\"").length();
        int posMetaDescrClose = pageBody.indexOf("\"", posMetaDescr + lengthMetaDescrField);
        if (posMetaDescr == -1 || posMetaDescrClose == -1) {
            // No description.
            return NO_DESCRIPTION;
        } else {
            String description = pageBody.substring(posMetaDescr + lengthMetaDescrField, posMetaDescrClose);
            return description;
        }
    }
}
