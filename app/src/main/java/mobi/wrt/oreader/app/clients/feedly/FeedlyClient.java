package mobi.wrt.oreader.app.clients.feedly;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONArray;

import java.util.List;
import java.util.Set;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.model.JSONModel;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.utils.ContentUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.AuthActivity;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.feedly.datasource.FeedlyDataSource;
import mobi.wrt.oreader.app.clients.feedly.datasource.PostDataSourceRequest;
import mobi.wrt.oreader.app.clients.feedly.db.Content;
import mobi.wrt.oreader.app.clients.feedly.processor.ContentProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.TestStringProcessor;
import mobi.wrt.oreader.app.image.IContentImage;

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
        public String[] getAdapterColumns(Uri meta) {
            return new String[]{Content.TITLE, Content.IMAGES, Content.STRIP_CONTENT, Content.PUBLISHED_AS_STRING};
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
    }

}