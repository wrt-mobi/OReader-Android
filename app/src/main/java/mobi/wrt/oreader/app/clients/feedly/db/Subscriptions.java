package mobi.wrt.oreader.app.clients.feedly.db;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.gson.GsonPrimitiveJoinerConverter;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class Subscriptions extends BaseEntity {

    @dbLong
    public static final String ADDED = "added";

    @dbLong
    public static final String UPDATED = "updated";

    @dbString
    public static final String SORT_ID = "sortid";

    @dbString
    public static final String TITLE = "title";

    @dbString
    public static final String VISUAL_URL = "visualUrl";

    @dbString
    public static final String WEBSITE = "website";

    @dbString
    public static final String CATEGORIES_JOINED = "categories_joined";

    //LOCAL
    @dbInteger
    public static final String COUNT = "count";

    public static final String META_DEFAULT_VALUE = "subscription";

    @Override
    public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {
        super.onBeforeListUpdate(dbHelper, db, dataSourceRequest, position, contentValues);
        Long id = contentValues.getAsLong(ID);
        if (id == null) {
            id = generateId(dbHelper, db, dataSourceRequest, contentValues);
            contentValues.put(ID, id);
        }
        ContentValues clientEntity = ClientEntity.create(contentValues.getAsString(TITLE), contentValues.getAsInteger(COUNT), ClientEntity.Rate.STREAM, META_DEFAULT_VALUE+contentValues.getAsString(ID_AS_STRING), null, id, ClientsFactory.Type.FEEDLY);
        dbHelper.updateOrInsert(dataSourceRequest, db, ClientEntity.class, clientEntity);
    }

    //TODO refactoring for other converter
    //only for parsing
    @dbEntities(clazz = Object.class, jsonConverter = CategoryConverter.class)
    public static final String CATEGORIES = "categories";

    public static class CategoryConverter extends GsonPrimitiveJoinerConverter {

        @Override
        public void convert(Params params) {
            StringBuilder tagsBuilder = new StringBuilder();
            JsonArray jsonArray = params.getJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement item = jsonArray.get(i);
                tagsBuilder.append(item.getAsJsonObject().get(Category.LABEL).getAsString());
                if (i != jsonArray.size()-1) {
                    tagsBuilder.append(getSplitter());
                }
            }
            String result = tagsBuilder.toString();
            if (!StringUtil.isEmpty(result)) {
                params.getContentValues().put(getEntityKey(), result);
            }
        }

        @Override
        public String getSplitter() {
            return ", ";
        }

        @Override
        public String getEntityKey() {
            return CATEGORIES_JOINED;
        }
    }
}
