package mobi.wrt.oreader.app.clients.feedly.datasource;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;

public class PostDataSourceRequest extends DataSourceRequest {

    private static final String BODY = "body";

    public PostDataSourceRequest(String requestDataUri, String data) {
        super(HttpAndroidDataSource.DefaultHttpRequestBuilder.getUrl(requestDataUri, HttpAndroidDataSource.DefaultHttpRequestBuilder.Type.POST));
        putParam(BODY, data);
    }

    public static String getBody(DataSourceRequest dataSourceRequest) {
        return dataSourceRequest.getParam(BODY);
    }
}
