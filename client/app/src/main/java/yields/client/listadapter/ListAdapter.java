package yields.client.listadapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.Inflater;

import yields.client.R;
import yields.client.yieldsapplication.YieldsApplication;

import yields.client.messages.Message;

public class ListAdapter extends ArrayAdapter<Message> {
    public ListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ListAdapter(Context context, int resource, List<Message> messages) {
        super(context, resource, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Log.d("DEBUG ------", "getView");
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.messagelayout, null);

        Message message = getItem(position);

        ImageView imageView = (ImageView) v.findViewById(R.id.profilpic);
        Bitmap image = BitmapFactory.decodeResource(YieldsApplication.getResources(), R.drawable.userpicture);
        image = getCroppedBitmap(image,80);
        imageView.setImageBitmap(image);

        LinearLayout userNameAndDateLayout = new LinearLayout(YieldsApplication.getApplicationContext());
        userNameAndDateLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout relativeLayout = new RelativeLayout(YieldsApplication.getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        TextView username  = new TextView(YieldsApplication.getApplicationContext());
        username.setText("Nicolas");
        username.setTextSize(10);
        username.setTextColor(Color.rgb(39, 89, 196));
        userNameAndDateLayout.addView(username);

        TextView date = new TextView(YieldsApplication.getApplicationContext());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time  = dateFormat.format(message.getDate());
        date.setText(time);
        date.setTextSize(10);
        date.setAlpha(0.6f);
        date.setTextColor(Color.GRAY);
        relativeLayout.addView(date, lp);


        LinearLayout usernameDate = (LinearLayout) v.findViewById(R.id.usernamedate);
        userNameAndDateLayout.addView(relativeLayout);
        usernameDate.addView(userNameAndDateLayout);

        LinearLayout contentLayout = (LinearLayout) v.findViewById(R.id.contentfield);
        contentLayout.addView(message.getContent().getView());
        return v;
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
                sbmp.getWidth() / 2+0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }

}
