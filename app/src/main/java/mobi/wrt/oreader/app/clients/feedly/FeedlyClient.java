package mobi.wrt.oreader.app.clients.feedly;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.AuthActivity;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.feedly.datasource.FeedlyDataSource;
import mobi.wrt.oreader.app.clients.feedly.db.Content;
import mobi.wrt.oreader.app.clients.feedly.processor.ContentProcessor;

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
            return new String[]{Content.TITLE, Content.STRIP_CONTENT};
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
            Cursor cursor = (Cursor) listFragment.getListAdapter().getItem(totalItemCount - 1);
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
    }

}