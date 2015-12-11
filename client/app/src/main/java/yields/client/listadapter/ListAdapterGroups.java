package yields.client.listadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
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
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Class used to represent a list of groups, in a listview
 */
public class ListAdapterGroups extends ArrayAdapter<Group> {
    private Context mContext;
    private int mGroupLayout;
    private List<Group> mGroups;

    public ListAdapterGroups(Context context, int groupLayout, List<Group> groups) {
        super(context, groupLayout, groups);
        mContext = context;
        mGroupLayout = groupLayout;
        mGroups = groups;
    }

    /**
     * Get the view at the position
     * @param position the position of the item
     * @param convertView The old view
     * @param parent The parent view
     * @return The new view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View singleGroup = inflater.inflate(mGroupLayout, parent, false);

        TextView textViewGroupName = (TextView) singleGroup.findViewById(R.id.textViewGroupName);
        TextView textViewGroupLastMessage = (TextView)
                singleGroup.findViewById(R.id.textViewGroupLastMessage);
        ImageView imageGroup = (ImageView) singleGroup.findViewById(R.id.imageViewGroup);
        ImageView imagePastille = (ImageView) singleGroup.findViewById(R.id.imageViewPastille);

        Group group = mGroups.get(position);

        textViewGroupName.setText(group.getName());
        textViewGroupLastMessage.setText(group.getPreviewOfLastMessage());

        Bitmap groupImage = group.getImage();
        imageGroup.setImageBitmap(GraphicTransforms.getCroppedCircleBitmap(groupImage,
                mContext.getResources().getInteger(R.integer.groupImageDiameter)));

        Bitmap pastille = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_message_black_24dp);

        int [] pixels = new int [pastille.getHeight()*pastille.getWidth()];

        pastille.getPixels(pixels, 0, pastille.getWidth(), 0, 0,
                pastille.getWidth(), pastille.getHeight());

        for(int i = 0; i < pixels.length; i++) {
            if(pixels[i] == Color.BLACK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pixels[i] = mContext.getResources().getColor(R.color.barColor, mContext.getTheme());
                }
                else {
                    pixels[i] = mContext.getResources().getColor(R.color.barColor);
                }
            }
            else {
                pixels[i] = Color.TRANSPARENT;
            }
        }

        pastille.setPixels(pixels, 0, pastille.getWidth(), 0, 0,
                pastille.getWidth(), pastille.getHeight());

        imagePastille.setImageBitmap(pastille);

        Log.d("hello", "erqhir");
        if (YieldsApplication.getBinder().hasPending(group.getId())) {
            imagePastille.setVisibility(View.VISIBLE);
        } else {
            imagePastille.setVisibility(View.GONE);
        }

        return singleGroup;
    }

    @Override
    public boolean isEnabled(int position) {
        Group group = mGroups.get(position);
        return group.isValidated();
    }

}