package mobi.wrt.oreader.app.content;

import by.istin.android.xcore.provider.DBContentProvider;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Category;
import mobi.wrt.oreader.app.clients.feedly.db.Content;
import mobi.wrt.oreader.app.clients.feedly.db.Subscriptions;
import mobi.wrt.oreader.app.clients.twitter.db.SearchTwitterProfile;

public class ContentProvider extends DBContentProvider {

    public static final Class<?>[] ENTITIES = new Class<?>[]{
            //FEEDLY
            Category.class,
            Subscriptions.class,
            ClientEntity.class,
            Content.class,

            //TWITTER
            SearchTwitterProfile.class
    };

    @Override
    public Class<?>[] getEntities() {
        return ENTITIES;
    }

}
