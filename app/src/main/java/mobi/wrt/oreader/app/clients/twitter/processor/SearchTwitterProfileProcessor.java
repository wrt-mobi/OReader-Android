package mobi.wrt.oreader.app.clients.twitter.processor;

import android.content.ContentValues;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.Log;
import mobi.wrt.oreader.app.clients.twitter.TwitterApi;
import mobi.wrt.oreader.app.clients.twitter.db.SearchTwitterProfile;

public class SearchTwitterProfileProcessor extends AbstractGsonBatchProcessor<ContentValues[]> {

    public static final String APP_SERVICE_KEY = "twitter:SearchTwitterProfileProcessor";

    public SearchTwitterProfileProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(SearchTwitterProfile.class, ContentValues[].class, contentProviderSupport);
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        super.onStartProcessing(dataSourceRequest, dbConnection);
        String param = dataSourceRequest.getParam(TwitterApi.Users.SEARCH_QUERY_PARAM);
        int delete = dbConnection.delete(DBHelper.getTableName(getClazz()), SearchTwitterProfile.SEARCH_QUERY + "=?", new String[]{param});
        Log.xd(this, "delete " + delete);
    }

    @Override
    protected void onProcessingFinish(DataSourceRequest dataSourceRequest, ContentValues[] contentValueses) throws Exception {
        super.onProcessingFinish(dataSourceRequest, contentValueses);
        notifyChange(getHolderContext(), getClazz());
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
