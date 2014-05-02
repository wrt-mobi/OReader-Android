package mobi.wrt.oreader.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.Log;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.feedly.FeedlyApi;
import mobi.wrt.oreader.app.clients.feedly.FeedlyAuthManager;
import mobi.wrt.oreader.app.clients.feedly.bo.AuthResponse;
import mobi.wrt.oreader.app.clients.feedly.datasource.FeedlyDataSource;
import mobi.wrt.oreader.app.clients.feedly.processor.CategoriesProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.SubscriptionsProcessor;


public class WizardActivity extends FragmentActivity {

    private ClientsFactory.IClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);
        AuthResponse authResponse = FeedlyAuthManager.getAuthResponse();
        if (authResponse != null) {
            Log.xd(this, authResponse.toString());
            DataSourceRequest dataSourceRequestSubscriptions = new DataSourceRequest(FeedlyApi.Subscriptions.PATH);
            dataSourceRequestSubscriptions.setCacheable(false);
            dataSourceRequestSubscriptions.setForceUpdateData(true);

            DataSourceRequest dataSourceRequestCategories = new DataSourceRequest(FeedlyApi.Categories.PATH);
            dataSourceRequestCategories.setCacheable(false);
            dataSourceRequestCategories.setForceUpdateData(true);

            DataSourceRequest.JoinedRequestBuilder joinedRequestBuilder = new DataSourceRequest.JoinedRequestBuilder(dataSourceRequestSubscriptions);
            joinedRequestBuilder.setDataSource(FeedlyDataSource.APP_SERVICE_KEY);
            joinedRequestBuilder.add(dataSourceRequestCategories, CategoriesProcessor.APP_SERVICE_KEY);

            DataSourceService.execute(this, joinedRequestBuilder.build(), SubscriptionsProcessor.APP_SERVICE_KEY, FeedlyDataSource.APP_SERVICE_KEY);
        } else {
            Log.xd(this, "is not logged");
        }
    }

    public void onFeedlyLoginClick(View view) {
        performLogin(ClientsFactory.Type.FEEDLY);
    }

    public void onVkLoginClick(View view) {
        performLogin(ClientsFactory.Type.VK);
    }

    public void onTwitterLoginClick(View view) {
        performLogin(ClientsFactory.Type.TWITTER);
    }

    public void onFacebookLoginClick(View view) {
        performLogin(ClientsFactory.Type.FACEBOOK);
    }

    private void performLogin(ClientsFactory.Type type) {
        mClient = ClientsFactory.get(this).getClient(type);
        mClient.performLogin(this);
    }

    public void onDoneClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mClient.handleLoginResult(this, requestCode, resultCode, data);
    }
}
