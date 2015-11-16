package yields.client.listadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

/**
 * Class used to represent a list of groups, in a compact way, for the
 * search activity
 */
public class ListAdapterSearchedGroups extends ArrayAdapter<Group> {
    private Context mContext;
    private int mGroupLayout;
    private List<Group> mGroups;

    public ListAdapterSearchedGroups(Context context, int groupLayout, List<Group> groups) {
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

        Group group = mGroups.get(position);

        View groupSearched = inflater.inflate(mGroupLayout, parent, false);

        ImageView imageGroupSearched = (ImageView) groupSearched.findViewById(
                R.id.imageGroupSearched);
        imageGroupSearched.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(group.getImage(),
                mContext.getResources().getInteger(R.integer.groupImageDiameter)));

        TextView textViewGroupSearched = (TextView) groupSearched.findViewById(
                R.id.textViewGroupSearched);
        textViewGroupSearched.setText(group.getName());

        return groupSearched;
    }
}