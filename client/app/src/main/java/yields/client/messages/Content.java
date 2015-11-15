package yields.client.messages;

import android.view.View;

import yields.client.exceptions.ContentException;

/**
 * Abstract class used to describe a class which acts as a Content. A Content has a type and
 * can generate a view for itself.
 */
public abstract class Content {

    public enum ContentType {
        TEXT("Text"), IMAGE("Image");

        private final String mType;

        ContentType(String type) {
            mType = type;
        }

        public String getType() {
            return mType;
        }

    }

    /**
     * Abstract default constructor.
     */
    public Content() {
    }

    /**
     * Returns a String which describes the type of the Content.
     *
     * @return A String which describes the type of the Content.
     */
    public abstract ContentType getType();

    /**
     * Returns a View which displays the data this Content contains.
     *
     * @return A View which displays the data this Content contains.
     * @throws ContentException If the data of this Content is incorrect.
     */
    public abstract View getView() throws ContentException;

    /**
     * Returns a preview of the content, displayed in the group list with the content's sender
     *
     * @return a string describing the content
     */
    public abstract String getPreview();
}
