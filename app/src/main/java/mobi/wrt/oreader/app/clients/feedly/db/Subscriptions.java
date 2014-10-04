package mobi.wrt.oreader.app.clients.feedly.db;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.Config;
import by.istin.android.xcore.annotations.converter.IConverter;
import by.istin.android.xcore.annotations.converter.gson.GsonConverter;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.Meta;
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

    @dbString(value = @Config(dbType = Config.DBType.STRING, transformer = Transformer.class))
    @SerializedName("categories")
    public static final String CATEGORIES_JOINED = "categories_joined";

    //LOCAL
    @dbInteger
    public static final String COUNT = "count";

    @Override
    public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {
        super.onBeforeListUpdate(dbHelper, db, dataSourceRequest, position, contentValues);
        Long id = contentValues.getAsLong(ID);
        if (id == null) {
            id = generateId(dbHelper, db, dataSourceRequest, contentValues);
            contentValues.put(ID, id);
        }
        String title = contentValues.getAsString(TITLE);
        String visualUrl = contentValues.getAsString(VISUAL_URL);

        String meta = Meta.buildMeta(ClientsFactory.Type.FEEDLY.name())
                .param(Meta.DB_ENTITY, DBHelper.getTableName(Subscriptions.class))
                .param(ID_AS_STRING, contentValues.getAsString(ID_AS_STRING))
                .param(WEBSITE, contentValues.getAsString(WEBSITE))
                .build();

        ContentValues clientEntity = ClientEntity.create(
                title,
                contentValues.getAsInteger(COUNT),
                ClientEntity.Rate.STREAM,
                meta,
                StringUtil.isEmpty(visualUrl) ? Meta.buildImageUrl(title) : visualUrl,
                id,
                ClientsFactory.Type.FEEDLY,
                contentValues.getAsString(CATEGORIES_JOINED));
        dbHelper.updateOrInsert(dataSourceRequest, db, ClientEntity.class, clientEntity);
    }

    public static class Transformer extends Config.AbstractTransformer<GsonConverter.Meta> {

        public static final IConverter<GsonConverter.Meta> CONVERTER = new GsonConverter() {
            @Override
            public void convert(ContentValues contentValues, String fieldValue, Object parent, Meta meta) {
                JsonElement jsonValue = meta.getJsonElement();
                if (jsonValue.isJsonPrimitive()) {
                    return;
                }
                StringBuilder tagsBuilder = new StringBuilder();
                JsonArray jsonArray = jsonValue.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonElement item = jsonArray.get(i);
                    tagsBuilder.append(item.getAsJsonObject().get(Category.LABEL).getAsString());
                    if (i != jsonArray.size()-1) {
                        tagsBuilder.append(", ");
                    }
                }
                String result = tagsBuilder.toString();
                contentValues.put(fieldValue, result);
            }
        };

        @Override
        public IConverter<GsonConverter.Meta> converter() {
            return CONVERTER;
        }

    }
}
