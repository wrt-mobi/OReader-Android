package mobi.wrt.oreader.app.clients.feedly.processor;

import android.content.ContentValues;
import android.os.Parcel;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.model.ParcelableModel;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.feedly.FeedlyApi;
import mobi.wrt.oreader.app.clients.feedly.db.Content;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class ContentProcessor extends AbstractGsonBatchProcessor<ContentProcessor.Response> {

    public static final String APP_SERVICE_KEY = "feedly:processor:content";

    public static class Response extends ParcelableModel {

        private ContentValues[] items;

        public static final Creator<Response> CREATOR = new Creator<Response>() {

            public Response createFromParcel(Parcel in) {
                return new Response(in);
            }

            public Response[] newArray(int size) {
                return new Response[size];
            }

        };

        public Response(final Parcel source) {
            items = readContentValuesArray(source);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            writeContentValuesArray(dest, items);
        }

        public ContentValues[] getItems() {
            return items;
        }
    }

    public ContentProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(Content.class, ContentProcessor.Response.class, contentProviderSupport);
    }


    @Override
    protected void onProcessingFinish(DataSourceRequest dataSourceRequest, ContentProcessor.Response contentValueses) {
        super.onProcessingFinish(dataSourceRequest, contentValueses);
        notifyChange(getHolderContext(), getClazz());
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        super.onStartProcessing(dataSourceRequest, dbConnection);
        String streamId = dataSourceRequest.getParam(FeedlyApi.Streams.STREAM_ID);
        String continuation = dataSourceRequest.getParam(FeedlyApi.Streams.CONTINUATION);
        if (StringUtil.isEmpty(continuation)) {
            dbConnection.delete(DBHelper.getTableName(Content.class), Content.STREAM_ID + "=?", new String[]{streamId});
        }
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

}