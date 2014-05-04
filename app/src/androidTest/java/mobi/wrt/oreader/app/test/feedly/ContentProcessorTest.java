package mobi.wrt.oreader.app.test.feedly;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.utils.BytesUtils;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.feedly.FeedlyApi;
import mobi.wrt.oreader.app.clients.feedly.db.Content;
import mobi.wrt.oreader.app.clients.feedly.processor.ContentProcessor;
import mobi.wrt.oreader.app.test.common.AbstractTestProcessor;

/**
 * Created by Uladzimir_Klyshevich on 4/23/2014.
 */
public class ContentProcessorTest extends AbstractTestProcessor {

    public static final String STREAM_ID = "user/de2328c6-dcf7-4aa4-b24c-48d79676bf63/category/Технологии";
    public static final String ENCODED_STREAM_ID = StringUtil.encode(STREAM_ID);

    public void testCategoryFeeds() throws Exception {
        clear(Content.class);
        ContentProcessor.Response response = (ContentProcessor.Response) testExecute(ContentProcessor.APP_SERVICE_KEY, "feedly/content_by_id.json?" + FeedlyApi.Streams.STREAM_ID + "=" + ENCODED_STREAM_ID);
        checkCount(response.getItems().length, ModelContract.getUri(Content.class), null, Content.STREAM_ID + "=?", new String[]{STREAM_ID}, null);
        byte[] bytes = BytesUtils.toByteArray(response);
        ContentProcessor.Response responseFromByteArray = BytesUtils.parcelableFromByteArray(ContentProcessor.Response.CREATOR, bytes);
        assertEquals(response.getItems().length, responseFromByteArray.getItems().length);
        /*checkRequiredFields(Category.class, Category.ID, Category.ID_AS_STRING, Category.LABEL, Category.POSITION);
        testExecute(CategoriesProcessor.APP_SERVICE_KEY, "feedly/categories_updated.json");
        checkCount(Category.class, 3);
        checkCount(ClientEntity.class, 3);
        checkRequiredFields(Category.class, Category.ID, Category.ID_AS_STRING, Category.LABEL, Category.POSITION);*/
    }
}
