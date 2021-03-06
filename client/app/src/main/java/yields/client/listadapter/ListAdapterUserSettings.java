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
import yields.client.activities.UserSettingsActivity;

/**
 * Class used to represent the list of settings of a user, in a listview
 */
public class ListAdapterUserSettings extends ArrayAdapter<String> {
    private Context mContext;
    private int mSettingLayout;
    private List<String> mSettings;

    /**
     * Constructor for the ListAdapterGroupSettings.
     *
     * @param context       The context of the application.
     * @param settingLayout The layout Settings.
     * @param settings      Text for the icons.
     */
    public ListAdapterUserSettings(Context context, int settingLayout, List<String> settings) {
        super(context, settingLayout, settings);
        mContext = context;
        mSettingLayout = settingLayout;
        mSettings = settings;
    }

    /**
     * Returns the View of the adapter.
     *
     * @param position    Position of the element.
     * @param convertView The View to convert.
     * @param parent      The parent of the view to be converted.
     * @return The new View respecting the layout.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View setting = inflater.inflate(mSettingLayout, parent, false);

        ImageView imageSetting = (ImageView) setting.findViewById(R.id.imageSetting);

        int idDrawable;
        UserSettingsActivity.Settings[] settings = UserSettingsActivity.Settings.values();
        switch (settings[position]) {
            case INFO:
                idDrawable = R.drawable.ic_info_black_24dp;
                break;

            case NAME:
                idDrawable = R.drawable.ic_mode_edit_black_24dp;
                break;

            case IMAGE:
                idDrawable = R.drawable.ic_photo_camera_black_24dp;
                break;

            case REMOVE_ENTOURAGE:
                idDrawable = R.drawable.ic_cancel_black_24dp;
                break;

            default:
                idDrawable = R.drawable.ic_exit_to_app_black_24dp;
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageSetting.setImageDrawable(mContext.getResources().getDrawable(idDrawable,
                    mContext.getTheme()));
        } else {
            imageSetting.setImageDrawable(mContext.getResources().getDrawable(idDrawable));
        }

        TextView textViewSetting = (TextView) setting.findViewById(R.id.textViewSetting);

        String text = mSettings.get(position);
        textViewSetting.setText(text);

        return setting;
    }

}