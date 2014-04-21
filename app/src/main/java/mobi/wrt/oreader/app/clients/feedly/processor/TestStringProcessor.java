package mobi.wrt.oreader.app.clients.feedly.processor;

import by.istin.android.xcore.processor.impl.AbstractStringProcessor;
import by.istin.android.xcore.utils.Log;
import mobi.wrt.oreader.app.clients.feedly.FeedlyAuthManager;
import mobi.wrt.oreader.app.clients.feedly.bo.AuthResponse;

public class TestStringProcessor extends AbstractStringProcessor<String> {

    public static final String APP_SERVICE_KEY = "oreader:processor:teststring:feedly:auth";

    @Override
    protected String convert(String string) throws Exception {
        Log.xd(this, string);
        return string;
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
