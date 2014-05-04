package mobi.wrt.oreader.app.clients.feedly.processor;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.Meta;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Subscriptions;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class SubscriptionsProcessor extends FeedlyBaseProcessor {

    public static final String APP_SERVICE_KEY = "feedly:processor:subscriptions";

    private static String META_FILTER = Meta.buildMeta(ClientsFactory.Type.FEEDLY.name())
            .param(Meta.DB_ENTITY, DBHelper.getTableName(Subscriptions.class)).build();

    public SubscriptionsProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(Subscriptions.class, contentProviderSupport);
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        super.onStartProcessing(dataSourceRequest, dbConnection);
        dbConnection.delete(DBHelper.getTableName(ClientEntity.class),
                ClientEntity.TYPE + "=? AND " + ClientEntity.META + " like ?",
                new String[]{ClientsFactory.Type.FEEDLY.name(), META_FILTER+"%"});
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

}