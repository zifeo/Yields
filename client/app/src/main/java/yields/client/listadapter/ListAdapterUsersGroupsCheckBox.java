package yields.client.listadapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import yields.client.R;
import yields.client.gui.GraphicTransforms;
import yields.client.node.Group;
import yields.client.node.User;

/**
 * Class used to represent a list of user and a list of groups
 */
public class ListAdapterUsersGroupsCheckBox extends ArrayAdapter<User> {
    private Context mContext;

    private List<User> mUsers;
    private List<Group> mGroups;

    /**
     * Constructor for ListAdapterUsersGroupsCheckBox
     * @param context The context of the app
     * @param users The list of users
     * @param groups The list of groups
     */
    public ListAdapterUsersGroupsCheckBox(Context context, int resource, List<User> users, List<Group> groups) {
        super(context, resource, users);
        mContext = context;
        mUsers = users;
        mGroups = groups;
    }

    @Override
    public int getCount() {
        return mUsers.size() + mGroups.size();
    }

    @Override
    public User getItem(int position) {
        return mUsers.get(0);
    }

    /**
     * Returns the View of the adapter.
     * @param position Position of the element.
     * @param convertView The View to convert.
     * @param parent The parent of the view to be conveted.
     * @return The new View respecting the layout.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < mUsers.size()){
            return getUserView(mUsers.get(position), parent);
        }
        else {
            return getGroupView(mGroups.get(position - mUsers.size()), parent);
        }
    }

    /**
     * Gets the view for the specified user
     * @param user The user we want to have a view for
     * @return The view associated with the user
     */
    private View getUserView(User user, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.add_user_layout, parent, false);

        TextView textViewUserName = (TextView) userView.findViewById(R.id.textViewUserName);
        ImageView imageUser = (ImageView) userView.findViewById(R.id.imageUser);
        CheckBox checkBox = (CheckBox) userView.findViewById(R.id.checkboxUser);

        textViewUserName.setText(user.getName());

        imageUser.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(user.getImg(),
                mContext.getResources().getInteger(R.integer.groupImageDiameter)));

        checkBox.setChecked(true);

        return userView;
    }

    /**
     * Gets the view for the specified group
     * @param group The group we want to have a view for
     * @return The view associated with the group
     */
    private View getGroupView(Group group, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View groupView = inflater.inflate(R.layout.add_group_layout, parent, false);

        TextView textViewGroupName = (TextView) groupView.findViewById(R.id.textViewGroupName);
        ImageView imageGroup = (ImageView) groupView.findViewById(R.id.imageGroup);
        CheckBox checkBox = (CheckBox) groupView.findViewById(R.id.checkboxGroup);

        textViewGroupName.setText(group.getName());

        imageGroup.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(group.getImage(),
                mContext.getResources().getInteger(R.integer.groupImageDiameter)));

        checkBox.setChecked(true);

        return groupView;
    }

    @Override
    public int getItemViewType(int position) {
        return IGNORE_ITEM_VIEW_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}