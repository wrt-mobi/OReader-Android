package mobi.wrt.oreader.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.ImageView;

public class VerticalWebView extends WebView {

    public VerticalWebView(Context context) {
        super(context);
    }

    public VerticalWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

   /* @Override
    public boolean onInterceptTouchEvent(MotionEvent p_event) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent p_event) {
        ViewParent parent = getParent();
        if (parent != null) {
            if (p_event.getAction() == MotionEvent.ACTION_MOVE) {
                parent.requestDisallowInterceptTouchEvent(true);
            } else {
                parent.requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.onTouchEvent(p_event);
    }*/
}
