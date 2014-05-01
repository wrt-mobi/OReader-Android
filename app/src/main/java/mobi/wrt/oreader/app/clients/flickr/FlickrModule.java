package mobi.wrt.oreader.app.clients.flickr;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import mobi.wrt.oreader.app.clients.flickr.processor.PhotosSearchProcessor;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class FlickrModule {

    public static void onCreate(CoreApplication coreApplication, IDBContentProviderSupport dbContentProvider) {
        coreApplication.registerAppService(new PhotosSearchProcessor());
    }

}
