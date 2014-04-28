package mobi.wrt.oreader.app.clients.feedly.processor;

import android.content.ContentValues;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import mobi.wrt.oreader.app.clients.feedly.db.Category;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class CategoriesProcessor extends AbstractGsonBatchProcessor<ContentValues[]> {

    public static final String APP_SERVICE_KEY = "feedly:processor:categories";

    public CategoriesProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(Category.class, ContentValues[].class, contentProviderSupport);
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        super.onStartProcessing(dataSourceRequest, dbConnection);
        dbConnection.delete(DBHelper.getTableName(Category.class), null, null);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

}