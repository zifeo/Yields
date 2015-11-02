package yields.client.listadapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import yields.client.gui.PairUserBoolean;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class ListAdapterUsersCheckBox extends ArrayAdapter<PairUserBoolean> {
    private Context mContext;
    private List<PairUserBoolean> mUsers;
    private boolean mRemoveWhenUnchecked;

    public ListAdapterUsersCheckBox(Context context, int addUserLayout, List<PairUserBoolean> users, boolean removeWhenUnchecked) {
        super(context, addUserLayout, users);
        mContext = context;
        mUsers = users;
        mRemoveWhenUnchecked = removeWhenUnchecked;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.add_user_layout, parent, false);

        TextView textViewUserName = (TextView) userView.findViewById(R.id.textViewUserName);
        ImageView imageUser = (ImageView) userView.findViewById(R.id.imageUser);
        CheckBox checkBox = (CheckBox) userView.findViewById(R.id.checkboxUser);

        User user = mUsers.get(position).getUser();

        textViewUserName.setText(user.getName());

        Bitmap userImage = user.getImg();
        if (userImage == null){
            imageUser.setImageBitmap(YieldsApplication.getDefaultUserImage());
        }
        else {
            imageUser.setImageBitmap(userImage);
        }

        boolean b = mUsers.get(position).getBoolean();
        checkBox.setChecked(b);

        final int pos = position;

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUsers.get(pos).setBoolean(isChecked);

                if (mRemoveWhenUnchecked && !isChecked && pos!=0){
                    mUsers.remove(pos);
                    ListAdapterUsersCheckBox.this.notifyDataSetChanged();
                }

                if (mRemoveWhenUnchecked  && pos==0){
                    buttonView.setChecked(!isChecked);
                }
            }
        });

        return userView;
    }
}