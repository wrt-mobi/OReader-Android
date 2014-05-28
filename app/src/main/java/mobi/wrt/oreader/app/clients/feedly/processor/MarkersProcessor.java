package mobi.wrt.oreader.app.clients.feedly.processor;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import java.io.Serializable;
import java.util.List;

import by.istin.android.xcore.processor.impl.AbstractGsonProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.ContentUtils;
import by.istin.android.xcore.utils.Log;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Content;

public class MarkersProcessor extends AbstractGsonProcessor<MarkersProcessor.Response> {

    public static final String APP_SERVICE_KEY = "oreader:feedly:markers";

    protected static class Response implements Serializable {

        protected static class Marker implements Serializable {

            private Integer count;
            private String id;
            private Long updated;

        }

        private List<Marker> unreadcounts;

    }

    public MarkersProcessor() {
        super(Response.class);
    }

    @Override
    public void cache(Context context, DataSourceRequest dataSourceRequest, Response response) throws Exception {
        List<Response.Marker> unreadcounts = response.unreadcounts;
        List<ContentValues> entities = ContentUtils.getEntities(context, ClientEntity.class, ClientEntity.TYPE + "=?", ClientsFactory.Type.FEEDLY.name());
        if (unreadcounts == null || unreadcounts.isEmpty()) {
            if (entities != null && !entities.isEmpty()) {
                for (ContentValues values : entities) {
                    ClientEntity.updateCount(values, 0);
                }
            }
        } else {
            if (entities != null && !entities.isEmpty()) {
                for (ContentValues values : entities) {
                    Uri meta = Uri.parse(values.getAsString(ClientEntity.META));
                    String idAsString = meta.getQueryParameter(Content.ID_AS_STRING);
                    Response.Marker found = null;
                    for (Response.Marker marker : unreadcounts) {
                        String markerId = marker.id;
                        if (idAsString.endsWith(markerId)) {
                            found = marker;
                            ClientEntity.updateCount(values, marker.count);
                            break;
                        }
                    }
                    if (found == null) {
                        ClientEntity.updateCount(values, 0);
                    } else {
                        unreadcounts.remove(found);
                    }
                }
            }
        }
        ContentUtils.putEntities(context, ClientEntity.class, entities);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
