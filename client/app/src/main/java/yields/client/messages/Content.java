package yields.client.messages;

import android.view.View;
import android.widget.LinearLayout;

import yields.client.exceptions.ContentException;

public interface Content {

    String getType();

    View getView() throws ContentException;
}
