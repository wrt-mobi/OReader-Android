package mobi.wrt.oreader.app.test.flickr;

import java.util.List;

import mobi.wrt.oreader.app.clients.flickr.processor.PhotosSearchProcessor;
import mobi.wrt.oreader.app.test.common.AbstractTestProcessor;

/**
 * Created by Uladzimir_Klyshevich on 4/23/2014.
 */
public class PhotosSearchProcessorTest extends AbstractTestProcessor {

    public void testPhotosSearchFeeds() throws Exception {
        PhotosSearchProcessor.Response response = (PhotosSearchProcessor.Response) testExecute(PhotosSearchProcessor.APP_SERVICE_KEY, "flickr/photos.search.json");
        List<PhotosSearchProcessor.Response.Photo> photos = response.getPhotos();
        assertNotNull(photos);
        assertEquals(10, photos.size());
        for (PhotosSearchProcessor.Response.Photo photo : photos) {
            assertNotNull(photo.getFarm());
            assertNotNull(photo.getId());
            assertNotNull(photo.getOwner());
            assertNotNull(photo.getSecret());
            assertNotNull(photo.getServer());
        }
    }
}
