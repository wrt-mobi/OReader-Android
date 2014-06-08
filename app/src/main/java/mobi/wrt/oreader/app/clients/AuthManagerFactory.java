package mobi.wrt.oreader.app.clients;


import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.utils.AppUtils;
import mobi.wrt.oreader.app.clients.feedly.FeedlyAuthManager;
import mobi.wrt.oreader.app.clients.twitter.TwitterAuthManager;

public class AuthManagerFactory implements XCoreHelper.IAppServiceKey {

    public static final String APP_SERVICE_KEY = "core:AuthManagerFactory";

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    public static AuthManagerFactory get(Context context) {
        return AppUtils.get(context, APP_SERVICE_KEY);
    }

    public static enum Type {
		FACEBOOK, TWITTER, VK, FEEDLY
	}

    private Map<Type, IAuthManager> mCache = new HashMap<Type, IAuthManager>();

    private final Object mLock = new Object();

	public IAuthManager getManager(Type type) {
        IAuthManager authManager = mCache.get(type);
        if (authManager == null) {
            synchronized (mLock) {
                authManager = mCache.get(type);
                if (authManager != null) {
                    return authManager;
                }
                switch (type) {
                    case TWITTER:
                        authManager = new TwitterAuthManager();
                        break;
                    case FEEDLY:
                        authManager = new FeedlyAuthManager();
                        break;
                }
                if (authManager == null) {
                    throw new IllegalArgumentException("unsupported type " + type);
                }
                mCache.put(type, authManager);
            }
        }
        return authManager;
	}
	
}
