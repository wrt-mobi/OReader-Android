package mobi.wrt.oreader.app.clients;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.utils.AppUtils;
import mobi.wrt.oreader.app.clients.feedly.FeedlyClient;

public class ClientsFactory implements XCoreHelper.IAppServiceKey {

    public static final String APP_SERVICE_KEY = "oreader:clientsfactory";

    public static ClientsFactory get(Context context) {
        return AppUtils.get(context, APP_SERVICE_KEY);
    }

    public ClientsFactory() {

    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    public static interface IClient {

        void performLogin(Activity activity);

        void handleLoginResult(Activity activity, int requestCode, int resultCode, Intent data);

        IContentsFragmentConnector getContentsFragmentConnector(Uri meta);

        public static interface IContentsFragmentConnector {

            String getUrl(Uri meta);

            String getProcessorKey(Uri meta);

            String[] getAdapterColumns(Uri meta);

            String getOrder(Uri meta);

            Uri getUri(Uri meta);

            boolean isPagingSupport(Uri meta);

            void onPageLoad(Uri meta, int newPage, int totalItemCount);
        }
    }

    private class EmptyClient implements IClient {

        @Override
        public void performLogin(Activity activity) {

        }

        @Override
        public void handleLoginResult(Activity activity, int requestCode, int resultCode, Intent data) {

        }

        @Override
        public IContentsFragmentConnector getContentsFragmentConnector(Uri meta) {
            return null;
        }

    }

    public static enum Type {
        FEEDLY, VK, FACEBOOK, TWITTER;
    }

    public IClient getClient(Type type) {
        //TODO lazy initialization with cache
        EmptyClient emptyClient = new EmptyClient();
        switch (type) {
            case FEEDLY:
                return new FeedlyClient();
            case VK:
                return emptyClient;
            case FACEBOOK:
                return emptyClient;
            case TWITTER:
                return emptyClient;
        }
        throw new IllegalArgumentException("unknown type");
    }

}
