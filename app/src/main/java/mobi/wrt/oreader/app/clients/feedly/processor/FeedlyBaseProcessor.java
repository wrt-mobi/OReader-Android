package mobi.wrt.oreader.app.clients.feedly.processor;

import android.content.ContentValues;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
abstract class FeedlyBaseProcessor extends AbstractGsonBatchProcessor<ContentValues[]> {

    public FeedlyBaseProcessor(Class<?> clazz, IDBContentProviderSupport contentProviderSupport) {
        super(clazz, ContentValues[].class, contentProviderSupport);
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        super.onStartProcessing(dataSourceRequest, dbConnection);
        dbConnection.delete(DBHelper.getTableName(getClazz()), null, null);
    }

    @Override
    protected void onProcessingFinish(DataSourceRequest dataSourceRequest, ContentValues[] contentValueses) throws Exception {
        super.onProcessingFinish(dataSourceRequest, contentValueses);
        notifyChange(getHolderContext(), getClazz());
    }
}