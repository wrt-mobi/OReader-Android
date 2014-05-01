package mobi.wrt.oreader.app.clients.flickr.processor;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.processor.impl.AbstractGsonProcessor;
import by.istin.android.xcore.source.DataSourceRequest;

public class PhotosSearchProcessor extends AbstractGsonProcessor<PhotosSearchProcessor.Response> {

    public static final String APP_SERVICE_KEY = "oreader:flickr:photos.search";

    public static class Response implements Serializable {

        public static class Photo implements Serializable {
            private String id;
            private String owner;
            private String secret;
            private String server;
            private Integer farm;

            public String getId() {
                return id;
            }

            public String getOwner() {
                return owner;
            }

            public String getSecret() {
                return secret;
            }

            public String getServer() {
                return server;
            }

            public Integer getFarm() {
                return farm;
            }

        }

        public static class Photos implements Serializable {

            private ArrayList<Photo> photo;
        }

        private Photos photos;

        public List<Photo> getPhotos() {
            return photos == null ? null : photos.photo;
        }
    }

    public PhotosSearchProcessor() {
        super(Response.class);
    }

    @Override
    public void cache(Context context, DataSourceRequest dataSourceRequest, Response response) throws Exception {
        //we will not cache images
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
