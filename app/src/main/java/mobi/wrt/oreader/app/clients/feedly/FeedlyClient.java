package mobi.wrt.oreader.app.clients.feedly;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.model.JSONModel;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.ContentUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.clients.AuthActivity;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.feedly.bo.AuthResponse;
import mobi.wrt.oreader.app.clients.feedly.datasource.FeedlyDataSource;
import mobi.wrt.oreader.app.clients.feedly.datasource.PostDataSourceRequest;
import mobi.wrt.oreader.app.clients.feedly.db.Content;
import mobi.wrt.oreader.app.clients.feedly.processor.CategoriesProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.ContentProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.MarkersProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.SubscriptionsProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.TestStringProcessor;
import mobi.wrt.oreader.app.image.IContentImage;
import mobi.wrt.oreader.app.ui.StreamConfig;

public class FeedlyClient implements ClientsFactory.IClient {

    @Override
    public void performLogin(Activity activity) {
        AuthActivity.get(activity, AuthManagerFactory.Type.FEEDLY);
    }

    @Override
    public void handleLoginResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public IContentsFragmentConnector getContentsFragmentConnector(Uri meta) {
        return new ContentsConnector();
    }

    @Override
    public void markAsRead(boolean isRead, final Set<Long> readIds) {
        if (readIds == null || readIds.isEmpty()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] filter = StringUtil.toStringArray(readIds);
                String selection = Content.ID + " in (" + StringUtil.makeJoinedPlaceholders("?", ",", readIds.size()) + ")";
                List<ContentValues> entities = ContentUtils.getEntities(ContextHolder.get(), new String[]{Content.ID_AS_STRING}, ModelContract.getUri(Content.class), null, selection, filter);
                if (entities == null) {
                    Log.xe(FeedlyClient.this, "entity is null");
                    return;
                }
                JSONModel jsonModel = new JSONModel();
                JSONArray jsonArray = new JSONArray();
                for (ContentValues contentValues : entities) {
                    jsonArray.put(contentValues.getAsString(Content.ID_AS_STRING));
                }
                jsonModel.set("entryIds", jsonArray);
                jsonModel.set("action", "markAsRead");
                jsonModel.set("type", "entries");
                PostDataSourceRequest postDataSourceRequest = new PostDataSourceRequest(FeedlyApi.Markers.MARKERS.build(), jsonModel.toString());
                DataSourceService.execute(ContextHolder.get(), postDataSourceRequest, TestStringProcessor.APP_SERVICE_KEY, FeedlyDataSource.APP_SERVICE_KEY);
            }
        }).start();
    }

    @Override
    public void addUpdateDataSourceRequest(Holder<DataSourceRequest.JoinedRequestBuilder> joinedRequestBuilder, long cacheExpiration, boolean forceUpdateData, boolean cacheable) {
        AuthResponse authResponse = FeedlyAuthManager.getAuthResponse();
        if (authResponse != null) {
            DataSourceRequest dataSourceRequestSubscriptions = new DataSourceRequest(FeedlyApi.Subscriptions.PATH);
            dataSourceRequestSubscriptions.setCacheable(cacheable);
            dataSourceRequestSubscriptions.setForceUpdateData(forceUpdateData);
            dataSourceRequestSubscriptions.setCacheExpiration(cacheExpiration);

            DataSourceRequest dataSourceRequestCategories = new DataSourceRequest(FeedlyApi.Categories.PATH);
            dataSourceRequestCategories.setCacheable(cacheable);
            dataSourceRequestCategories.setCacheExpiration(cacheExpiration);
            dataSourceRequestCategories.setForceUpdateData(forceUpdateData);

            DataSourceRequest dataSourceRequestMarkers = new DataSourceRequest(FeedlyApi.Markers.COUNTS_PATH);
            dataSourceRequestMarkers.setCacheable(cacheable);
            dataSourceRequestMarkers.setCacheExpiration(cacheExpiration);
            dataSourceRequestMarkers.setForceUpdateData(forceUpdateData);

            DataSourceRequest.JoinedRequestBuilder requestBuilder;
            if (joinedRequestBuilder.isNull()) {
                requestBuilder = new DataSourceRequest.JoinedRequestBuilder(dataSourceRequestSubscriptions);
                requestBuilder.setDataSource(FeedlyDataSource.APP_SERVICE_KEY);
                requestBuilder.setProcessor(SubscriptionsProcessor.APP_SERVICE_KEY);
                joinedRequestBuilder.set(requestBuilder);
            } else {
                requestBuilder = joinedRequestBuilder.get();
                requestBuilder.add(dataSourceRequestSubscriptions, SubscriptionsProcessor.APP_SERVICE_KEY, FeedlyDataSource.APP_SERVICE_KEY);
            }
            requestBuilder.add(dataSourceRequestCategories, CategoriesProcessor.APP_SERVICE_KEY, FeedlyDataSource.APP_SERVICE_KEY);
            requestBuilder.add(dataSourceRequestMarkers, MarkersProcessor.APP_SERVICE_KEY, FeedlyDataSource.APP_SERVICE_KEY);
        } else {
            Log.xd(this, "is not logged");
        }
    }

    private class ContentsConnector implements IContentsFragmentConnector {

        @Override
        public String getUrl(Uri meta) {
            return FeedlyApi.Streams.CONTENTS.build(StringUtil.encode(meta.getQueryParameter(Content.ID_AS_STRING)), "true", "0", StringUtil.EMPTY);
        }

        @Override
        public String getProcessorKey(Uri meta) {
            return ContentProcessor.APP_SERVICE_KEY;
        }

        @Override
        public String[] getAdapterColumns(StreamConfig.AdapterType adapterType, Uri meta) {
            switch (adapterType) {
                case DEFAULT:
                    return new String[]{Content.TITLE, Content.IMAGES, Content.STRIP_CONTENT, Content.PUBLISHED_AS_STRING};
                case FULL:
                    return new String[]{Content.TITLE, Content.IMAGES, Content.PUBLISHED_AS_STRING};
                default:
                    throw new IllegalArgumentException("unsupported adapter type");
            }
        }

        @Override
        public String getOrder(Uri meta) {
            return Content.POSITION + " asc";
        }

        @Override
        public Uri getUri(Uri meta) {
            return ModelContract.getUri(Content.class);
        }

        @Override
        public boolean isPagingSupport(Uri meta) {
            return true;
        }

        @Override
        public void onPageLoad(XListFragment listFragment, Uri meta, int newPage, int totalItemCount) {
            int realAdapterCount = XListFragment.getRealAdapterCount(listFragment.getListAdapter());
            Cursor cursor = (Cursor) listFragment.getListAdapter().getItem(realAdapterCount - 1);
            String continuation = CursorUtils.getString(Content.ID_AS_STRING, cursor);
            String url = FeedlyApi.Streams.CONTENTS.build(StringUtil.encode(meta.getQueryParameter(Content.ID_AS_STRING)), "true", String.valueOf(totalItemCount), continuation);
            listFragment.loadData(listFragment.getActivity(), url, getUrl(meta));
        }

        @Override
        public String getDataSourceKey(Uri meta) {
            return FeedlyDataSource.APP_SERVICE_KEY;
        }

        @Override
        public String getSelection(Uri meta) {
            return Content.STREAM_ID + "=?";
        }

        @Override
        public String[] getSelectionArgs(Uri meta) {
            return new String[]{meta.getQueryParameter(Content.ID_AS_STRING)};
        }

        @Override
        public List<IContentImage> getImagesFromContent(Cursor cursor) {
            return Content.getImages(cursor);
        }

        @Override
        public CursorModel.CursorModelCreator getCursorModelCreator(StreamConfig.AdapterType adapterType, Uri meta) {
            switch (adapterType) {
                case DEFAULT:
                    return CursorModel.CursorModelCreator.DEFAULT;
                case FULL:
                    return new CursorModel.CursorModelCreator() {
                        @Override
                        public CursorModel create(Cursor cursor) {
                            return new FullCursorModel(cursor);
                        }
                    };
                default:
                    throw new IllegalArgumentException("unsupported adapter type");
            }
        }

        @Override
        public void onAdapterGetView(StreamConfig.AdapterType adapterType, long itemId, Cursor cursor, int position, View view) {
            if (adapterType == StreamConfig.AdapterType.FULL) {
                ((TextView)view.findViewById(R.id.description)).setText(((FullCursorModel)cursor).mHtmlCache.get(itemId), TextView.BufferType.SPANNABLE);
            }
        }

        @Override
        public int[] getAdapterControlIds(StreamConfig.AdapterType adapterType) {
            switch (adapterType) {
                case DEFAULT:
                    return new int[]{R.id.label, R.id.imagesViewGroup, R.id.description, R.id.date};
                case FULL:
                    return new int[]{R.id.label, R.id.imagesViewGroup, R.id.date};
            }
            throw new IllegalArgumentException("unsupported adapter type");
        }

        private class FullCursorModel extends CursorModel {

            private ConcurrentHashMap<Long, Spanned> mHtmlCache = new ConcurrentHashMap<Long, Spanned>();

            public FullCursorModel(Cursor cursor) {
                super(cursor);
            }

            @Override
            public void doInBackground(final Context context) {
                super.doInBackground(context);
                mHtmlCache.clear();
                if (!CursorUtils.isEmpty(this)) {
                    moveToFirst();
                    int count = getCount();
                    for (int i = 0; i < count; i++) {
                        CursorModel item = get(i);
                        String summaryContent = item.getString(Content.SUMMARY_CONTENT);
                        if (StringUtil.isEmpty(summaryContent)) {
                            summaryContent = item.getString(Content.CONTENT_CONTENT);
                        }
                        if (summaryContent == null) {
                            summaryContent = StringUtil.EMPTY;
                        }
                        mHtmlCache.put(item.getLong(Content._ID), Html.fromHtml(summaryContent, new Html.ImageGetter(){

                            @Override
                            public Drawable getDrawable(String source) {
                                return context.getResources().getDrawable(android.R.color.transparent);
                            }
                        }, null));
                    }
                }
            }

            @Override
            public void close() {
                super.close();
                mHtmlCache.clear();
            }
        }
    }

}