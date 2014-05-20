package mobi.wrt.oreader.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.View;

import by.istin.android.xcore.preference.PreferenceHelper;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.Log;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.feedly.FeedlyApi;
import mobi.wrt.oreader.app.clients.feedly.FeedlyAuthManager;
import mobi.wrt.oreader.app.clients.feedly.bo.AuthResponse;
import mobi.wrt.oreader.app.clients.feedly.datasource.FeedlyDataSource;
import mobi.wrt.oreader.app.clients.feedly.processor.CategoriesProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.MarkersProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.SubscriptionsProcessor;


public class WizardActivity extends FragmentActivity {

    private static final String PREF_IS_WIZARD_DONE = "pref_is_wizard_done";

    private ClientsFactory.IClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceHelper.getBoolean(PREF_IS_WIZARD_DONE, false)) {
            onDoneClick(null);
            return;
        }
        setContentView(R.layout.activity_wizard);
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
        PreferenceHelper.set(PREF_IS_WIZARD_DONE, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mClient.handleLoginResult(this, requestCode, resultCode, data);
    }
}
