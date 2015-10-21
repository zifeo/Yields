package yields.client.gui;

import android.content.Context;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CenteredToolBar extends Toolbar {

    public CenteredToolBar(Context context) {
        super(context);
    }

    public CenteredToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CenteredToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (child instanceof ActionMenuView) {
            params.width = LayoutParams.MATCH_PARENT;
        }
        super.addView(child, params);
    }
}