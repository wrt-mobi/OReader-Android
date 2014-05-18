package mobi.wrt.oreader.app.view.utils;

import android.app.Activity;
import android.view.ViewGroup;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import by.istin.android.xcore.utils.UiUtil;

public class TranslucentUtils {

    public static void applyTranslucentPaddingForView(ViewGroup view, boolean statusBar, boolean actionBar, boolean bottomBar) {
        if (UiUtil.hasKitKat()) {
            SystemBarTintManager tintManager = new SystemBarTintManager((Activity) view.getContext());
            SystemBarTintManager.SystemBarConfig systemBarConfig = tintManager.getConfig();
            view.setClipToPadding(false);
            int topPadding = view.getPaddingTop() + (actionBar ? systemBarConfig.getActionBarHeight() : 0) + (statusBar ? systemBarConfig.getStatusBarHeight() : 0);
            int bottomPadding = view.getPaddingBottom() + (bottomBar ? systemBarConfig.getNavigationBarHeight() : 0);
            view.setPadding(view.getPaddingLeft(), topPadding, view.getPaddingRight(), bottomPadding);
        }
    }
}
