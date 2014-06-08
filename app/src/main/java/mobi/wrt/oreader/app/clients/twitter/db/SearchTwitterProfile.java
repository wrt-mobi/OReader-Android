package mobi.wrt.oreader.app.clients.twitter.db;

import android.content.ContentValues;

import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IBeforeUpdate;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import mobi.wrt.oreader.app.clients.twitter.TwitterApi;

public class SearchTwitterProfile extends TwitterProfile implements IBeforeUpdate {

    //LOCAL
    @dbString
    public static String SEARCH_QUERY = "q";

    @Override
    public void onBeforeUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues) {
        String param = dataSourceRequest.getParam(TwitterApi.Users.SEARCH_QUERY_PARAM);
        contentValues.put(SEARCH_QUERY, param);
    }
}
