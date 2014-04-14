package mobi.wrt.oreader.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import mobi.wrt.oreader.app.clients.ClientsFactory;


public class WizardActivity extends FragmentActivity {

    private ClientsFactory.IClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mClient = new ClientsFactory().getClient(type);
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
