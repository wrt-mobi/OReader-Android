package mobi.wrt.oreader.app.clients.feedly;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.AuthActivity;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
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
            return FeedlyApi.Streams.CONTENTS.build(meta.getQueryParameter(Content.ID_AS_STRING), "true", StringUtil.EMPTY);
        }

        @Override
        public String getProcessorKey(Uri meta) {
            return ContentProcessor.APP_SERVICE_KEY;
        }

        @Override
        public String[] getAdapterColumns(Uri meta) {
            return new String[]{ClientEntity.ICON, ClientEntity.TITLE, ClientEntity.TYPE, ClientEntity.COUNT_AS_STRING};
        }

        @Override
        public String getOrder(Uri meta) {
            return null;
        }

        @Override
        public Uri getUri(Uri meta) {
            return ModelContract.getUri(Content.class);
        }

        @Override
        public boolean isPagingSupport(Uri meta) {
            return false;
        }

        @Override
        public void onPageLoad(Uri meta, int newPage, int totalItemCount) {

        }
    }

}