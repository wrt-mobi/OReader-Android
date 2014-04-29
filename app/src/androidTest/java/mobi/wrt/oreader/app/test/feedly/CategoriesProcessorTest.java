package mobi.wrt.oreader.app.test.feedly;

import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Category;
import mobi.wrt.oreader.app.clients.feedly.processor.CategoriesProcessor;
import mobi.wrt.oreader.app.test.common.AbstractTestProcessor;

/**
 * Created by Uladzimir_Klyshevich on 4/23/2014.
 */
public class CategoriesProcessorTest extends AbstractTestProcessor {

    public void testCategoryFeeds() throws Exception {
        clear(Category.class);
        clear(ClientEntity.class);
        testExecute(CategoriesProcessor.APP_SERVICE_KEY, "feedly/categories.json");
        checkCount(Category.class, 5);
        checkCount(ClientEntity.class, 5);
        checkRequiredFields(Category.class, Category.ID, Category.ID_AS_STRING, Category.LABEL, Category.POSITION);
        testExecute(CategoriesProcessor.APP_SERVICE_KEY, "feedly/categories_updated.json");
        checkCount(Category.class, 3);
        checkCount(ClientEntity.class, 3);
        checkRequiredFields(Category.class, Category.ID, Category.ID_AS_STRING, Category.LABEL, Category.POSITION);
    }
}
