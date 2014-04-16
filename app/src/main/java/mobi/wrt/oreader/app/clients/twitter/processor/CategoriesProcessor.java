package mobi.wrt.oreader.app.clients.twitter.processor;

import by.istin.android.xcore.model.JSONModel;
import by.istin.android.xcore.processor.impl.AbstractStringProcessor;

public class CategoriesProcessor extends AbstractStringProcessor<JSONModel> {

    public static final String APP_SERVICE_KEY = "oreader:processor:twitter:auth";

    @Override
    protected JSONModel convert(String string) throws Exception {
        return new JSONModel(string);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
