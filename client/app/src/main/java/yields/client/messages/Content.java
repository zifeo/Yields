package yields.client.messages;

import android.view.View;
import android.widget.LinearLayout;

import yields.client.exceptions.ContentException;

/**
 * Interface used to describe a class which acts as a Content. A Content has a type and
 * can generate a view for itself.
 */
public interface Content {

    /**
     * Returns a String which describes the type of the Content.
     * @return A String which describes the type of the Content.
     */
    String getType();

    /**
     * Returns a View which displays the data this Content contains.
     * @return A View which displays the data this Content contains.
     * @throws ContentException If the data of this Content is incorrect.
     */
    View getView() throws ContentException;
}
