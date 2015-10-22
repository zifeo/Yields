package yields.client.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import yields.client.R;
import yields.client.node.Group;

public class ListAdapterGroups extends ArrayAdapter<Group> {
    private Context mContext;
    private int mGroupLayout;
    private List<Group> mGroups;

    public ListAdapterGroups(Context context, int groupLayout, List<Group> groups) {
        super(context, groupLayout, groups);
        mContext = context;
        mGroupLayout = groupLayout;
        mGroups = groups;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View singleGroup = inflater.inflate(R.layout.group_layout, parent, false);

        TextView textViewGroupName = (TextView) singleGroup.findViewById(R.id.textViewGroupName);
        TextView textViewGroupLastMessage = (TextView) singleGroup.findViewById(R.id.textViewGroupLastMessage);
        ImageView imageGroup = (ImageView) singleGroup.findViewById(R.id.imageGroup);

        textViewGroupName.setText(mGroups.get(position).getName());
        textViewGroupLastMessage.setText(mGroups.get(position).getPreviewOfLastMessage());
        imageGroup.setImageResource(R.drawable.ic_explore_black_24dp);

        return singleGroup;
    }
}