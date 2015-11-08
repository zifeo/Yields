package yields.client.listadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import yields.client.R;
import yields.client.gui.GraphicTransforms;
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

    /**
     * Get the view at the position
     * @param position the position of the item
     * @param convertView The old view
     * @param parent The parent view
     * @return The new view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View singleGroup = inflater.inflate(mGroupLayout, parent, false);

        TextView textViewGroupName = (TextView) singleGroup.findViewById(R.id.textViewGroupName);
        TextView textViewGroupLastMessage = (TextView)
                singleGroup.findViewById(R.id.textViewGroupLastMessage);
        ImageView imageGroup = (ImageView) singleGroup.findViewById(R.id.imageGroup);

        Group group = mGroups.get(position);

        textViewGroupName.setText(group.getName());
        textViewGroupLastMessage.setText(group.getPreviewOfLastMessage());

        Bitmap groupImage = group.getImage();
        imageGroup.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(groupImage,
                mContext.getResources().getInteger(R.integer.groupImageDiameter)));

        return singleGroup;
    }

    @Override
    public boolean isEnabled(int position) {
        Group group = mGroups.get(position);
        return group.isValidated();
    }

}