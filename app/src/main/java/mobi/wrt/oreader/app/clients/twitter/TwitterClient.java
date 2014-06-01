package mobi.wrt.oreader.app.clients.twitter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.util.Set;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.Holder;
import mobi.wrt.oreader.app.clients.AuthActivity;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.ClientsFactory;

public class TwitterClient implements ClientsFactory.IClient {

    @Override
    public void performLogin(Activity activity) {
        AuthActivity.get(activity, AuthManagerFactory.Type.TWITTER);
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
