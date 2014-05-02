package mobi.wrt.oreader.app.clients.feedly;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import mobi.wrt.oreader.app.clients.feedly.datasource.FeedlyDataSource;
import mobi.wrt.oreader.app.clients.feedly.processor.AuthFeedlyProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.CategoriesProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.MarkersProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.SubscriptionsProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.TestStringProcessor;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class FeedlyModule {

    public static void onCreate(CoreApplication coreApplication, IDBContentProviderSupport dbContentProvider) {
        coreApplication.registerAppService(new FeedlyDataSource());
        coreApplication.registerAppService(new AuthFeedlyProcessor());

        coreApplication.registerAppService(new CategoriesProcessor(dbContentProvider));
        coreApplication.registerAppService(new SubscriptionsProcessor(dbContentProvider));
        coreApplication.registerAppService(new MarkersProcessor());

        coreApplication.registerAppService(new TestStringProcessor());
    }

}
