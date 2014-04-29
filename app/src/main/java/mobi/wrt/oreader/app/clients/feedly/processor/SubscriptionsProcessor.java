package mobi.wrt.oreader.app.clients.feedly.processor;

import by.istin.android.xcore.provider.IDBContentProviderSupport;
import mobi.wrt.oreader.app.clients.feedly.db.Subscriptions;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class SubscriptionsProcessor extends FeedlyBaseProcessor {

    public static final String APP_SERVICE_KEY = "feedly:processor:subscriptions";

    public SubscriptionsProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(Subscriptions.class, contentProviderSupport);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

}