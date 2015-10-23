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
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class ListAdapterUsers extends ArrayAdapter<User> {
    private Context mContext;
    private int mGroupLayout;
    private List<User> mUsers;

    public ListAdapterUsers(Context context, int groupLayout, List<User> users) {
        super(context, groupLayout, users);
        mContext = context;
        mGroupLayout = groupLayout;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.add_user_layout, parent, false);

        TextView textViewUserName = (TextView) userView.findViewById(R.id.textViewUserName);
        ImageView imageUser = (ImageView) userView.findViewById(R.id.imageUser);

        User user = mUsers.get(position);

        textViewUserName.setText(user.getName());

        Bitmap userImage = user.getImg();
        if (userImage == null){
            imageUser.setImageBitmap(YieldsApplication.getDefaultUserImage());
        }
        else {
            imageUser.setImageBitmap(userImage);
        }

        return userView;
    }

}