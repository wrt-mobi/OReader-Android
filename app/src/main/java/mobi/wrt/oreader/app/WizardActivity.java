package mobi.wrt.oreader.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.source.DataSourceRequest;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.feedly.FeedlyApi;
import mobi.wrt.oreader.app.clients.feedly.datasource.FeedlyDataSource;
import mobi.wrt.oreader.app.clients.feedly.processor.TestStringProcessor;


public class WizardActivity extends FragmentActivity {

    private ClientsFactory.IClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);
        DataSourceRequest dataSourceRequest = new DataSourceRequest(FeedlyApi.Subscriptions.PATH);
        dataSourceRequest.setCacheable(false);
        dataSourceRequest.setForceUpdateData(true);
        DataSourceService.execute(this, dataSourceRequest, TestStringProcessor.APP_SERVICE_KEY, FeedlyDataSource.APP_SERVICE_KEY);
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
