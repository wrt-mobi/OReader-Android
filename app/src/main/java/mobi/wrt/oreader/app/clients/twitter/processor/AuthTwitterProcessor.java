package mobi.wrt.oreader.app.clients.twitter.processor;

import by.istin.android.xcore.processor.impl.AbstractStringProcessor;
import mobi.wrt.oreader.app.clients.twitter.bo.UserItem;

public class AuthTwitterProcessor extends AbstractStringProcessor<UserItem> {

    public static final String APP_SERVICE_KEY = "oreader:processor:twitter:auth";

    @Override
    protected UserItem convert(String string) throws Exception {
        return new UserItem(string);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
