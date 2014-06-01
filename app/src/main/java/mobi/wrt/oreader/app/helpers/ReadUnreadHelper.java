package mobi.wrt.oreader.app.helpers;

import java.util.HashSet;
import java.util.Set;

import by.istin.android.xcore.XCoreHelper;

public class ReadUnreadHelper implements XCoreHelper.IAppServiceKey {

    public static final String APP_SERVICE_KEY = "ReadUnreadHelper:key";

    private final Object mLock = new Object();

    private Set<Long> mAlreadyReadIds = new HashSet<Long>();

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    public boolean isNotRead(long itemId) {
        synchronized (mLock) {
            return !mAlreadyReadIds.contains(itemId);
        }
    }

    public void markAsRead(long itemId) {
        synchronized (mLock) {
            mAlreadyReadIds.add(itemId);
        }
    }

    public Set<Long> getIds() {
        synchronized (mLock) {
            return mAlreadyReadIds;
        }
    }
}
