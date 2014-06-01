package mobi.wrt.oreader.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import by.istin.android.xcore.preference.PreferenceHelper;
import mobi.wrt.oreader.app.clients.ClientsFactory;


public class WizardActivity extends FragmentActivity {

    private static final String PREF_IS_WIZARD_DONE = "pref_is_wizard_done";

    public static final String EXTRA_IGNORE_PREFERENCE = "extra_ignore_preference";

    private ClientsFactory.IClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceHelper.getBoolean(PREF_IS_WIZARD_DONE, false) && !getIntent().getBooleanExtra(EXTRA_IGNORE_PREFERENCE, false)) {
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
