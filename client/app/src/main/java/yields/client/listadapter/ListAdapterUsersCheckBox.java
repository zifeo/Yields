package yields.client.listadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import yields.client.R;
import yields.client.gui.GraphicTransforms;
import yields.client.node.User;

/**
 * Class used to represent a list of user, each with a checkbox
 */
public class ListAdapterUsersCheckBox extends ArrayAdapter<Map.Entry<User, Boolean>> {
    private Context mContext;

    // List of users with a boolean indicating if the user is currently selected
    private List<Map.Entry<User, Boolean>> mUsers;
    private List<User> mOriginalUsers; // list of users who cannot be unchecked
    private boolean mRemoveWhenUnchecked;

    /**
     * Constructor for ListAdapterUsersCheckBox
     * @param context The context of the app
     * @param addUserLayout The id of the basic view
     * @param users The list of users, each with a boolean, indicating if the user should be checked
     * @param removeWhenUnchecked If the user should be removed when the checkbox becomes unchecked
     * (used only to prevent the removing of the first user)
     */
    public ListAdapterUsersCheckBox(Context context,
                                    int addUserLayout,
                                    List<Map.Entry<User, Boolean>> users,
                                    boolean removeWhenUnchecked) {
        super(context, addUserLayout, users);
        mContext = context;
        mUsers = users;
        mRemoveWhenUnchecked = removeWhenUnchecked;
        mOriginalUsers = new ArrayList<>();

        for (int i = 0; i < users.size(); i++){
            if (users.get(i).getValue()){
                mOriginalUsers.add(users.get(i).getKey());
            }
        }
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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.add_user_layout, parent, false);

        TextView textViewUserName = (TextView) userView.findViewById(R.id.textViewUserName);
        ImageView imageUser = (ImageView) userView.findViewById(R.id.imageUser);
        CheckBox checkBox = (CheckBox) userView.findViewById(R.id.checkboxUser);

        User user = mUsers.get(position).getKey();

        Log.d("name", user.getName());

        textViewUserName.setText(user.getName());

        imageUser.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(user.getImg(),
                mContext.getResources().getInteger(R.integer.groupImageDiameter)));

        boolean b = mUsers.get(position).getValue();
        checkBox.setChecked(b);

        final int pos = position;

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mOriginalUsers.contains(mUsers.get(pos).getKey())){
                    buttonView.setChecked(true);
                }
                else {
                    mUsers.get(pos).setValue(isChecked);

                    // Cannot remove the first user
                    if (mRemoveWhenUnchecked && !isChecked && pos!=0){
                        mUsers.remove(pos);
                        ListAdapterUsersCheckBox.this.notifyDataSetChanged();
                    }

                    // Cannot remove the first user, need to recheck the box
                    if (mRemoveWhenUnchecked  && pos==0){
                        buttonView.setChecked(!isChecked);
                    }
                }
            }
        });

        return userView;
    }
}