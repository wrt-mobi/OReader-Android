package mobi.wrt.oreader.app.clients;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import java.util.List;
import java.util.Set;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.Holder;
import mobi.wrt.oreader.app.clients.feedly.FeedlyClient;
import mobi.wrt.oreader.app.clients.twitter.TwitterClient;
import mobi.wrt.oreader.app.image.IContentImage;
import mobi.wrt.oreader.app.ui.StreamConfig;

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

    public DataSourceRequest.JoinedRequestBuilder getUpdateDataSourceRequest(long cacheExpiration, boolean forceUpdateData, boolean cacheable) {
        Holder<DataSourceRequest.JoinedRequestBuilder> holderJoinedRequestBuilder = new Holder<DataSourceRequest.JoinedRequestBuilder>();
        for (Type type : Type.values()) {
            getClient(type).addUpdateDataSourceRequest(holderJoinedRequestBuilder, cacheExpiration, forceUpdateData, cacheable);
        }
        if (holderJoinedRequestBuilder.isNull()) {
            return null;
        }
        return holderJoinedRequestBuilder.get();
    }

    public static interface IClient {

        void performLogin(Activity activity);

        void handleLoginResult(Activity activity, int requestCode, int resultCode, Intent data);

        IContentsFragmentConnector getContentsFragmentConnector(Uri meta);

        void markAsRead(boolean isRead, Set<Long> readIds);

        void addUpdateDataSourceRequest(Holder<DataSourceRequest.JoinedRequestBuilder> joinedRequestBuilder, long cacheExpiration, boolean forceUpdateData, boolean cacheable);

        public static interface IContentsFragmentConnector {

            String getUrl(Uri meta);

            String getProcessorKey(Uri meta);

            String[] getAdapterColumns(StreamConfig.AdapterType adapterType, Uri meta);

            String getOrder(Uri meta);

            Uri getUri(Uri meta);

            boolean isPagingSupport(Uri meta);

            void onPageLoad(XListFragment listFragment, Uri meta, int newPage, int totalItemCount);

            String getDataSourceKey(Uri meta);

            String getSelection(Uri meta);

            String[] getSelectionArgs(Uri meta);

            List<IContentImage> getImagesFromContent(Cursor cursor);

            CursorModel.CursorModelCreator getCursorModelCreator(StreamConfig.AdapterType adapterType, Uri meta);

            void onAdapterGetView(StreamConfig.AdapterType adapterType, long itemId, Cursor cursor, int position, View view);

            int[] getAdapterControlIds(StreamConfig.AdapterType adapterType);
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

        @Override
        public void markAsRead(boolean isRead, Set<Long> readIds) {

        }

        @Override
        public void addUpdateDataSourceRequest(Holder<DataSourceRequest.JoinedRequestBuilder> joinedRequestBuilder, long cacheExpiration, boolean forceUpdateData, boolean cacheable) {

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
                return new TwitterClient();
        }
        throw new IllegalArgumentException("unknown type");
    }

}
