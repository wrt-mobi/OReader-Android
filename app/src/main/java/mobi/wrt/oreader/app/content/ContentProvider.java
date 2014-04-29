package mobi.wrt.oreader.app.content;

import by.istin.android.xcore.provider.DBContentProvider;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Category;
import mobi.wrt.oreader.app.clients.feedly.db.Subscriptions;

public class ContentProvider extends DBContentProvider {

    public static final Class<?>[] ENTITIES = new Class<?>[]{
            //FEEDLY
            Category.class,
            Subscriptions.class,
            ClientEntity.class
    };

    @Override
    public Class<?>[] getEntities() {
        return ENTITIES;
    }

}
