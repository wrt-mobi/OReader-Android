package mobi.wrt.oreader.app.content;

import by.istin.android.xcore.provider.DBContentProvider;
import mobi.wrt.oreader.app.clients.feedly.db.Category;

public class ContentProvider extends DBContentProvider {

    public static final Class<?>[] ENTITIES = new Class<?>[]{
            //FEEDLY
            Category.class
    };

    @Override
    public Class<?>[] getEntities() {
        return ENTITIES;
    }

}
