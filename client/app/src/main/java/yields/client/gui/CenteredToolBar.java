package yields.client.gui;

import android.content.Context;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * A custom toolbar used for centered action icons
 */
public class CenteredToolBar extends Toolbar {

    /**
     * Constructor for the CenteredToolBar.
     *
     * @param context The context of the app.
     */
    public CenteredToolBar(Context context) {
        super(context);
    }

    /**
     * Constructor for the CenteredToolBar.
     *
     * @param context The context of the app.
     * @param attrs   The set of attributes.
     */
    public CenteredToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor for the CenteredToolBar.
     *
     * @param context      The context of the app.
     * @param attrs        The set of attributes.
     * @param defStyleAttr The default style attribute.
     */
    public CenteredToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Add a View to the toolbar.
     *
     * @param child  The View to be added.
     * @param params The Layout params for the child.
     */
    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (child instanceof ActionMenuView) {
            params.width = LayoutParams.MATCH_PARENT;
        }
        super.addView(child, params);
    }
}