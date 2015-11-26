package yields.client.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

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
    public ListAdapterUsersGroupsCheckBox(Context context, int resource, List<User> users,
                                          List<Group> groups) {
        super(context, resource, users);
        mContext = context;
        mUsers = users;
        mGroups = groups;
    }

    /**
     * Return the number of item in this adapter
     * @return the number of item in this adapter
     */
    @Override
    public int getCount() {
        return mUsers.size() + mGroups.size();
    }

    /**
     * Get an item of the list
     * (Must be overridden to avoid access to non-existing users)
     * @param unused
     * @return The first user
     */
    @Override
    public User getItem(int unused) {
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
            return getUserView(position, parent);
        }
        else {
            return getGroupView(position - mUsers.size(), parent);
        }
    }

    /**
     * Gets the view for the specified user
     * @param pos The pos of the user we want to have a view for
     * @return The view associated with the user
     */
    private View getUserView(final int pos, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.add_user_layout, parent, false);

        User user = mUsers.get(pos);

        TextView textViewUserName = (TextView) userView.findViewById(R.id.textViewUserName);
        ImageView imageUser = (ImageView) userView.findViewById(R.id.imageUser);
        CheckBox checkBox = (CheckBox) userView.findViewById(R.id.checkboxUser);

        textViewUserName.setText(user.getName());

        imageUser.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(user.getImg(),
                mContext.getResources().getInteger(R.integer.groupImageDiameter)));

        checkBox.setChecked(true);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Cannot remove the first user
                if (pos != 0) {
                    mUsers.remove(pos);
                    ListAdapterUsersGroupsCheckBox.this.notifyDataSetChanged();
                }
                else {
                    buttonView.setChecked(true);
                }
            }
        });

        return userView;
    }

    /**
     * Gets the view for the specified group
     * @param  pos The pos of the group we want to have a view for
     * @return The view associated with the group
     */
    private View getGroupView(final int pos, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View groupView = inflater.inflate(R.layout.add_group_layout, parent, false);

        Group group = mGroups.get(pos);

        TextView textViewGroupName = (TextView) groupView.findViewById(R.id.textViewGroupName);
        ImageView imageGroup = (ImageView) groupView.findViewById(R.id.imageGroup);
        CheckBox checkBox = (CheckBox) groupView.findViewById(R.id.checkboxGroup);

        textViewGroupName.setText(group.getName());

        imageGroup.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(group.getImage(),
                mContext.getResources().getInteger(R.integer.groupImageDiameter)));

        checkBox.setChecked(true);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mGroups.remove(pos);
                ListAdapterUsersGroupsCheckBox.this.notifyDataSetChanged();
            }
        });

        return groupView;
    }
}