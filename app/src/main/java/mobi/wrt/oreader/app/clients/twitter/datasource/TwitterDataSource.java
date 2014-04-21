package mobi.wrt.oreader.app.clients.twitter.datasource;

import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.twitter.exception.TwitterAuthException;

public class TwitterDataSource extends HttpAndroidDataSource {

    public static final String APP_SERVICE_KEY = "oreader:twitter:httpdatasource";

    public TwitterDataSource() {
        super(new DefaultHttpRequestBuilder(){
            @Override
            public HttpRequestBase build(DataSourceRequest dataSourceRequest) throws IOException {
                HttpRequestBase httpRequestBase = super.build(dataSourceRequest);
                try {
                    AuthManagerFactory.getManager(AuthManagerFactory.Type.TWITTER).sign(httpRequestBase);
                } catch (Exception e) {
                    throw new TwitterAuthException(e);
                }
                return httpRequestBase;
            }
        }, new DefaultResponseStatusHandler());
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
