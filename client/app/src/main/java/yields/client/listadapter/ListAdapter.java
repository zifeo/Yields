package yields.client.listadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import yields.client.exceptions.MessageViewException;
import yields.client.messages.Message;
import yields.client.messages.MessageView;
import yields.client.yieldsapplication.YieldsApplication;

public class ListAdapter extends ArrayAdapter<Message> {

    public ListAdapter(Context context, int resource, List<Message> messages) {
        super(context, resource, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);
        MessageView messageView = null;
        try {
            messageView = new MessageView(YieldsApplication.getApplicationContext(), message);
        } catch (MessageViewException e) {
            e.printStackTrace();
        }
        return messageView;
    }
}
