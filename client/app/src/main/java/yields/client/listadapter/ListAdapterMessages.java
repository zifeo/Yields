package yields.client.listadapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import yields.client.exceptions.MessageViewException;
import yields.client.messages.Message;
import yields.client.messages.MessageView;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Adapter used for a ListView which allows it to display MessageViews as items.
 */
public class ListAdapterMessages extends ArrayAdapter<Message> {

    /**
     * Mains constructor, creates a ListAdapter from the Application's context,
     * a resource, and a List of Messages.
     * @param context The context of the Application.
     * @param resource The resource id for the ListView.
     * @param messages The List of Messages that will be displayed on the ListView.
     */
    public ListAdapterMessages(Context context, int resource, List<Message> messages) {
        super(context, resource, messages);
    }

    /**
     * Returns a view for every Message in the ListView
     * @param position The position of the Message in the ListView.
     * @param convertView The view to be converted.
     * @param parent The parent of the returned View.
     * @return A view for the Message at the given position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);
        MessageView messageView = null;
        int tag = this.getCount();
        try {
            messageView = new MessageView(YieldsApplication.getApplicationContext(), message);
            messageView.setTag((Object) tag);
        } catch (MessageViewException e) {
            Log.d("ListAdapterMessages", "Couldn't add view to message list");
        }
        return messageView;
    }
}
