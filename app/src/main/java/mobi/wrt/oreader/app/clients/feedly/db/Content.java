package mobi.wrt.oreader.app.clients.feedly.db;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.JsonSubJSONObject;
import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.HashUtils;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.FeedlyApi;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class Content extends BaseEntity {

    @dbLong
    public static final String PUBLISHED = "published";

    @dbString
    @JsonSubJSONObject
    @SerializedName("summary:content")
    public static final String SUMMARY_CONTENT = "summary_content";

    @dbString
    @JsonSubJSONObject
    @SerializedName("visual:url")
    public static final String VISUAL_URL = "visual_url";

    @dbString
    @JsonSubJSONObject
    @SerializedName("visual:height")
    public static final String VISUAL_HEIGHT = "visual_height";

    @dbString
    @JsonSubJSONObject
    @SerializedName("visual:width")
    public static final String VISUAL_WIDTH = "visual_width";

    @dbString
    public static final String TITLE = "title";

    @dbBoolean
    public static final String UNREAD = "unread";

    //LOCAL
    @dbString
    public static final String STREAM_ID = "stream_id";

    @Override
    public long generateId(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues) {
        return HashUtils.generateId(contentValues.getAsString(ID_AS_STRING), dataSourceRequest.getParam(FeedlyApi.Streams.STREAM_ID));
    }

    @Override
    public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {
        super.onBeforeListUpdate(dbHelper, db, dataSourceRequest, position, contentValues);
        Long id = contentValues.getAsLong(ID);
        if (id == null) {
            id = generateId(dbHelper, db, dataSourceRequest, contentValues);
            contentValues.put(ID, id);
        }
        contentValues.put(STREAM_ID, dataSourceRequest.getParam(FeedlyApi.Streams.STREAM_ID));
    }

}
