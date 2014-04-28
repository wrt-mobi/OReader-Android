package mobi.wrt.oreader.app.clients.feedly.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.entity.IGenerateID;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.HashUtils;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class Category implements BaseColumns, IBeforeArrayUpdate, IGenerateID {

    @dbLong
    public static final String ID = _ID;

    @dbString
    public static final String ID_AS_STRING = "id";

    @dbString
    public static final String LABEL = "label";

    //LOCAL
    @dbInteger
    public static final String POSITION = "position";

    @Override
    public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {
        contentValues.put(POSITION, position);
    }

    @Override
    public long generateId(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues) {
        return HashUtils.generateId(contentValues.getAsString(ID_AS_STRING));
    }
}
