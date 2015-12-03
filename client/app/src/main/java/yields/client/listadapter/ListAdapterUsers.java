package yields.client.listadapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import yields.client.R;
import yields.client.gui.GraphicTransforms;
import yields.client.node.User;

/**
 * Class used to represent a list of user
 */
public class ListAdapterUsers extends ArrayAdapter<User> {
    private Context mContext;
    private List<User> mUsers;
    private int mUserLayout;

    /**
     * Constructor for ListAdapterUsersCheckBox
     * @param context The context of the app
     * @param userLayout The id of the basic view
     * @param users The list of users
     */
    public ListAdapterUsers(Context context, int userLayout, List<User> users) {
        super(context, userLayout, users);
        mContext = context;
        mUsers = users;
        mUserLayout = userLayout;
    }

    /**
     * Return the number of item in this adapter
     * @return the number of item in this adapter
     */
    @Override
    public int getCount() {
        return mUsers.size() + 1;
    }

    /**
     * Returns the View of the adapter.
     * @param position Position of the element.
     * @param convertView The View to convert.
     * @param parent The parent of the view to be converted.
     * @return The new View respecting the layout.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < mUsers.size()){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View userView = inflater.inflate(mUserLayout, parent, false);

            TextView textViewUserName = (TextView) userView.findViewById(R.id.textViewUserName);
            ImageView imageUser = (ImageView) userView.findViewById(R.id.imageUser);

            User user = mUsers.get(position);

            textViewUserName.setText(user.getName());

            imageUser.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(user.getImg(),
                    mContext.getResources().getInteger(R.integer.groupImageDiameter)));

            return userView;
        }
        else {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.group_settings_layout, parent, false);

            ImageView image = (ImageView) view.findViewById(R.id.imageSetting);

            int idDrawable = R.drawable.ic_person_add_black_24dp;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                image.setImageDrawable(mContext.getResources().getDrawable(idDrawable,
                        mContext.getTheme()));
            } else {
                image.setImageDrawable(mContext.getResources().getDrawable(idDrawable));
            }

            TextView textView = (TextView) view.findViewById(R.id.textViewSetting);

            textView.setText("Add a new contact");

            return view;
        }
    }
}