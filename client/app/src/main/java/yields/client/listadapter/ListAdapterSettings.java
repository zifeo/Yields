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
import yields.client.activities.GroupSettingsActivity;

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

        int idDrawable = R.drawable.ic_mode_edit_black_24dp;
        if (position == GroupSettingsActivity.Settings.IMAGE.ordinal()){
            idDrawable = R.drawable.ic_photo_camera_black_24dp;
        }
        else if (position == GroupSettingsActivity.Settings.USERS.ordinal()){
            idDrawable = R.drawable.ic_group_black_24dp;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageSetting.setImageDrawable(mContext.getResources().getDrawable(idDrawable, mContext.getTheme()));
        } else {
            imageSetting.setImageDrawable(mContext.getResources().getDrawable(idDrawable));
        }

        TextView textViewSetting = (TextView) setting.findViewById(R.id.textViewSetting);

        String text = mSettings.get(position);
        textViewSetting.setText(text);


        return setting;
    }

}