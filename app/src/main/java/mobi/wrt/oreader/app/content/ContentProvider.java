package mobi.wrt.oreader.app.content;

import by.istin.android.xcore.provider.DBContentProvider;

public class ContentProvider extends DBContentProvider {

    public static final Class<?>[] ENTITIES = new Class<?>[]{};

    @Override
    public Class<?>[] getEntities() {
        return ENTITIES;
    }

}
