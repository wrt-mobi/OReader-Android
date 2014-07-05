package mobi.wrt.oreader.app.image;

import android.content.Context;
import android.net.Uri;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.Core;
import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.Meta;
import mobi.wrt.oreader.app.clients.flickr.FlickrApi;
import mobi.wrt.oreader.app.clients.flickr.processor.PhotosSearchProcessor;

public class CustomImageDownloader extends BaseImageDownloader {

    public static final String[] ASSETS_THUBS = {
        "assets://backgrounds/airport.png",
        "assets://backgrounds/club.png",
        "assets://backgrounds/concert_croud.png",
        "assets://backgrounds/croud.png",
        "assets://backgrounds/day_1.png",
        "assets://backgrounds/day_2.png",
        "assets://backgrounds/evening_1.png",
        "assets://backgrounds/evening_2.png",
        "assets://backgrounds/livingroom.png",
        "assets://backgrounds/morning_1.png",
        "assets://backgrounds/morning_2.png",
        "assets://backgrounds/night_1.png",
        "assets://backgrounds/night_2.png",
        "assets://backgrounds/night_3.png",
        "assets://backgrounds/street.png"
    };

    public CustomImageDownloader(Context context) {
        super(context);
    }

    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {
        Uri uri = Uri.parse(imageUri);
        if (uri.getScheme().equals(Meta.SCHEME_VALUE)) {
            final Holder<PhotosSearchProcessor.Response> responseHolder = new Holder<PhotosSearchProcessor.Response>();
            Core.ExecuteOperationBuilder<PhotosSearchProcessor.Response> executeOperationBuilder = new Core.ExecuteOperationBuilder<PhotosSearchProcessor.Response>();
            String host = uri.getHost();
            DataSourceRequest pDataSourceRequest = new DataSourceRequest(FlickrApi.Photos.SEARCH.build(StringUtil.encode(host), "1", "10"));
            pDataSourceRequest.setCacheable(false);
            pDataSourceRequest.setForceUpdateData(true);
            executeOperationBuilder
                    .setDataSourceKey(HttpAndroidDataSource.SYSTEM_SERVICE_KEY)
                    .setSuccess(new ISuccess<PhotosSearchProcessor.Response>() {
                        @Override
                        public void success(PhotosSearchProcessor.Response o) {
                            responseHolder.set(o);
                        }
                    })
                    .setProcessorKey(PhotosSearchProcessor.APP_SERVICE_KEY)
                    .setDataSourceRequest(pDataSourceRequest);
            try {
                Core.get(ContextHolder.get()).executeSync(executeOperationBuilder.build());
            } catch (Exception e) {
                return getStreamFromAssets(ASSETS_THUBS[new Random().nextInt(ASSETS_THUBS.length-1)], extra);
            }
            if (responseHolder.isNull() || responseHolder.get().getPhotos() == null || responseHolder.get().getPhotos().isEmpty()) {
                return getStreamFromAssets(ASSETS_THUBS[new Random().nextInt(ASSETS_THUBS.length-1)], extra);
            }
            List<PhotosSearchProcessor.Response.Photo> photos = responseHolder.get().getPhotos();
            PhotosSearchProcessor.Response.Photo photo = photos.get(new Random().nextInt(photos.size()));
            imageUri = StringUtil.format(FlickrApi.Photos.PHOTO_URL, photo.getFarm().toString(), photo.getServer(), photo.getId(), photo.getSecret());
        }
        Log.xd(this, "imgurl:"+imageUri);
        InputStream stream = super.getStream(imageUri, extra);
        return stream;
    }

}
