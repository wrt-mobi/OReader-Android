package mobi.wrt.oreader.app.clients.feedly.processor;

import by.istin.android.xcore.processor.impl.AbstractStringProcessor;
import mobi.wrt.oreader.app.clients.feedly.FeedlyAuthManager;
import mobi.wrt.oreader.app.clients.feedly.bo.AuthResponse;

public class AuthFeedlyProcessor extends AbstractStringProcessor<AuthResponse> {

    public static final String APP_SERVICE_KEY = "oreader:processor:feedly:auth";

    @Override
    protected AuthResponse convert(String string) throws Exception {
        AuthResponse authResponse = new AuthResponse(string);
        FeedlyAuthManager.save(authResponse);
        return authResponse;
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
