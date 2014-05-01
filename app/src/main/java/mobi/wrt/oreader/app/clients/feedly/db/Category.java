package mobi.wrt.oreader.app.clients.feedly.db;

import android.content.ContentValues;

import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class Category extends BaseEntity {

    @dbString
    public static final String LABEL = "label";

    //LOCAL
    @dbInteger
    public static final String COUNT = "count";
    public static final String META_DEFAULT_VALUE = "category";

    @Override
    public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {
        super.onBeforeListUpdate(dbHelper, db, dataSourceRequest, position, contentValues);
        Long id = contentValues.getAsLong(ID);
        if (id == null) {
            id = generateId(dbHelper, db, dataSourceRequest, contentValues);
            contentValues.put(ID, id);
        }
        String label = contentValues.getAsString(LABEL);
        ContentValues clientEntity = ClientEntity.create(
                label,
                contentValues.getAsInteger(COUNT),
                ClientEntity.Rate.FOLDER,
                META_DEFAULT_VALUE+contentValues.getAsString(ID_AS_STRING),
                "oreader://"+label,
                id,
                ClientsFactory.Type.FEEDLY);
        dbHelper.updateOrInsert(dataSourceRequest, db, ClientEntity.class, clientEntity);
    }
}
