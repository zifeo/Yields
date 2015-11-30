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
     * Returns the View of the adapter.
     * @param position Position of the element.
     * @param convertView The View to convert.
     * @param parent The parent of the view to be converted.
     * @return The new View respecting the layout.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
}