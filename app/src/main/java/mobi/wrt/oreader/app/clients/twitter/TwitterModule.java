package mobi.wrt.oreader.app.clients.twitter;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.twitter.datasource.TwitterDataSource;
import mobi.wrt.oreader.app.clients.twitter.processor.AuthTwitterProcessor;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class TwitterModule {

    public static void onCreate(CoreApplication coreApplication, IDBContentProviderSupport dbContentProvider) {
        AuthManagerFactory.initTw("lzZHjpd4yBf5AYz9Sc0oqkNSA", "8ZS1qmJgGGTxKuFbvDOC88Jc5au0O32rU8nNyqGIf49nrF1p3d");
        coreApplication.registerAppService(new TwitterDataSource());
        coreApplication.registerAppService(new AuthTwitterProcessor());
    }

}
