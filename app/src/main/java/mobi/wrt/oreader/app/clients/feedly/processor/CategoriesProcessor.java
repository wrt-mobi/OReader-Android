package mobi.wrt.oreader.app.clients.feedly.processor;

import by.istin.android.xcore.provider.IDBContentProviderSupport;
import mobi.wrt.oreader.app.clients.feedly.db.Category;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class CategoriesProcessor extends FeedlyBaseProcessor {

    public static final String APP_SERVICE_KEY = "feedly:processor:categories";

    public CategoriesProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(Category.class, contentProviderSupport);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

}