package mobi.wrt.oreader.app.clients.twitter;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import mobi.wrt.oreader.app.clients.twitter.datasource.TwitterDataSource;
import mobi.wrt.oreader.app.clients.twitter.processor.AuthTwitterProcessor;
import mobi.wrt.oreader.app.clients.twitter.processor.SearchTwitterProfileProcessor;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class TwitterModule {

    public static void onCreate(CoreApplication coreApplication, IDBContentProviderSupport dbContentProvider) {
        coreApplication.registerAppService(new TwitterDataSource());
        coreApplication.registerAppService(new AuthTwitterProcessor());
        coreApplication.registerAppService(new SearchTwitterProfileProcessor(dbContentProvider));
    }

}
