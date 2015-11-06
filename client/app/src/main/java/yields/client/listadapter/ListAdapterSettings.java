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
import yields.client.activities.GroupSettingsActivity;
import yields.client.gui.GraphicTransforms;
import yields.client.node.Group;

public class ListAdapterSettings extends ArrayAdapter<String> {
    private Context mContext;
    private int mSettingLayout;
    private List<String> mSettings;

    public ListAdapterSettings(Context context, int settingLayout, List<String> settings) {
        super(context, settingLayout, settings);
        mContext = context;
        mSettingLayout = settingLayout;
        mSettings = settings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View setting = inflater.inflate(mSettingLayout, parent, false);

        ImageView imageSetting = (ImageView) setting.findViewById(R.id.imageSetting);

        if (position == GroupSettingsActivity.Settings.USERS.ordinal()){
            imageSetting.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_group_black_24dp));
        }
        else {
            imageSetting.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_share_black_24dp));
        }

        TextView textViewSetting = (TextView) setting.findViewById(R.id.textViewSetting);

        String text = mSettings.get(position);
        textViewSetting.setText(text);


        return setting;
    }

}