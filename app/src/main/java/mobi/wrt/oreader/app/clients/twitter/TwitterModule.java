package mobi.wrt.oreader.app.clients.twitter;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.feedly.datasource.FeedlyDataSource;
import mobi.wrt.oreader.app.clients.feedly.processor.AuthFeedlyProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.CategoriesProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.TestStringProcessor;
import mobi.wrt.oreader.app.clients.twitter.datasource.TwitterDataSource;
import mobi.wrt.oreader.app.clients.twitter.processor.AuthTwitterProcessor;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class TwitterModule {

    public static void onCreate(CoreApplication coreApplication, IDBContentProviderSupport dbContentProvider) {
        AuthManagerFactory.initTw("gEu05wPZ3zJTWas5bDf1Ow", "MiDej7peU8wJkf93Rsq9gt8wLiwkXNW8KYsLxFBw");
        coreApplication.registerAppService(new TwitterDataSource());
        coreApplication.registerAppService(new AuthTwitterProcessor());
    }

}
