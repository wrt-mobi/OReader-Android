package mobi.wrt.oreader.app.clients.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IGenerateID;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.HashUtils;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.ClientsFactory;

public class ClientEntity implements BaseColumns, IGenerateID {

    @dbLong
    public static final String ID = _ID;

    @dbString
    public static final String TITLE = "title";

    @dbString
    public static final String COUNT_AS_STRING = "count_as_string";

    @dbInteger
    public static final String COUNT = "count";

    @dbInteger
    public static final String STARRED = "starred";

    public static enum Rate {
        FOLDER, SOCIAL, STREAM
    }

    @dbInteger
    //will use for sort: 0 - folder, 1 - social network, 2 - stream
    public static final String RATE = "rate";

    @dbString
    public static final String META = "meta";

    @dbString
    public static final String ICON = "icon";

    @dbString
    public static final String INTERNAL_ID = "internalId";

    @dbString
    public static final String TYPE = "type";

    @dbString
    public static final String CATEGORIES = "categories";

    @Override
    public long generateId(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues) {
        String type = contentValues.getAsString(TYPE);
        String internalId = contentValues.getAsString(INTERNAL_ID);
        return generateId(type, internalId);
    }

    public static long generateId(String type, String internalId) {
        return HashUtils.generateId(type, internalId);
    }

    public static ContentValues create(String title, Integer count, Rate rate, String meta, String icon, Long internalId, ClientsFactory.Type type, String categories) {
        if (count == null) {
            count = 0;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, title);
        updateCount(contentValues, count);
        contentValues.put(RATE, rate.ordinal());
        contentValues.put(META, meta);
        contentValues.put(STARRED, 0);
        contentValues.put(ICON, icon);
        contentValues.put(INTERNAL_ID, internalId);
        contentValues.put(TYPE, type.name());
        contentValues.put(CATEGORIES, StringUtil.isEmpty(categories) ? null : categories);
        return contentValues;
    }

    public static void updateCount(ContentValues contentValues, int count) {
        contentValues.put(COUNT, count);
        contentValues.put(COUNT_AS_STRING, count > 99 ? "99+" : String.valueOf(count));
    }
}
