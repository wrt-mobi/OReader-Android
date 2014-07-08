package mobi.wrt.oreader.app.utils;

import by.istin.android.xcore.preference.PreferenceHelper;

public class PreferenceUtils {

    public static final String HOME_VIEW_TYPE = "home_view_type";

    public static final String IS_HIDE_READ = "is_hide_read";

    public static enum HomeViewType {
        GRID, LIST
    }

    public static void setHomeViewType(HomeViewType homeViewType) {
        PreferenceHelper.set(HOME_VIEW_TYPE, homeViewType.ordinal());
    }

    public static HomeViewType getHomeViewType() {
        return HomeViewType.values()[PreferenceHelper.getInt(HOME_VIEW_TYPE, 0)];
    }

    public static void setHideRead(boolean isHideRead) {
        PreferenceHelper.set(IS_HIDE_READ, isHideRead);
    }

    public static boolean isHideRead() {
        return PreferenceHelper.getBoolean(IS_HIDE_READ, true);
    }
}
