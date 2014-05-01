package mobi.wrt.oreader.app.clients.flickr;

import by.istin.android.xcore.utils.UrlBuilder;
import mobi.wrt.oreader.app.BuildConfig;

public class FlickrApi {

    public static final UrlBuilder BASE_PATH = UrlBuilder.https("api.flickr.com/services/rest/?format=json&api_key="+BuildConfig.FLICKR_API_KEY+"&nojsoncallback=1");

    public static class Photos {

        public static final UrlBuilder SEARCH = UrlBuilder.parent(BASE_PATH).
                param("method", "flickr.photos.search")
                .param("text").param("page").param("per_page");

        public static final String PHOTO_URL = "http://farm%s.staticflickr.com/%s/%s_%s_z.jpg";
    }

}
