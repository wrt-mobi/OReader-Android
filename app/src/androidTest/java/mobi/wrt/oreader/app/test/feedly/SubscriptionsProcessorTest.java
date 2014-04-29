package mobi.wrt.oreader.app.test.feedly;

import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Subscriptions;
import mobi.wrt.oreader.app.clients.feedly.processor.SubscriptionsProcessor;
import mobi.wrt.oreader.app.test.common.AbstractTestProcessor;

/**
 * Created by Uladzimir_Klyshevich on 4/23/2014.
 */
public class SubscriptionsProcessorTest extends AbstractTestProcessor {

    public void testCategoryFeeds() throws Exception {
        clear(Subscriptions.class);
        clear(ClientEntity.class);

        testExecute(SubscriptionsProcessor.APP_SERVICE_KEY, "feedly/subscriptions.json");
        checkCount(Subscriptions.class, 5);
        checkCount(ClientEntity.class, 5);

        checkRequiredFields(Subscriptions.class,
                Subscriptions.ID,
                Subscriptions.ID_AS_STRING,
                Subscriptions.TITLE,
                Subscriptions.WEBSITE,
                Subscriptions.CATEGORIES_JOINED,
                Subscriptions.UPDATED,
                Subscriptions.POSITION);
        testExecute(SubscriptionsProcessor.APP_SERVICE_KEY, "feedly/subscriptions_updated.json");
        checkCount(Subscriptions.class, 4);
        checkCount(ClientEntity.class, 4);
        checkRequiredFields(Subscriptions.class,
                Subscriptions.ID,
                Subscriptions.ID_AS_STRING,
                Subscriptions.TITLE,
                Subscriptions.WEBSITE,
                //removed from one of item Subscriptions.CATEGORIES_JOINED,
                Subscriptions.UPDATED,
                Subscriptions.POSITION);
    }
}
