package mobi.wrt.oreader.app.clients.feedly.datasource;

import android.net.ParseException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.source.impl.http.exception.IOStatusException;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.feedly.exception.FeedlyAuthException;
import mobi.wrt.oreader.app.clients.twitter.exception.TwitterAuthException;

public class FeedlyDataSource extends HttpAndroidDataSource {

    public static final String APP_SERVICE_KEY = "oreader:feedly:httpdatasource";

    public FeedlyDataSource() {
        super(new DefaultHttpRequestBuilder(){
            @Override
            public HttpRequestBase build(DataSourceRequest dataSourceRequest) throws IOException {
                HttpRequestBase httpRequestBase = super.build(dataSourceRequest);
                AuthManagerFactory.getManager(AuthManagerFactory.Type.FEEDLY).sign(httpRequestBase);
                return httpRequestBase;
            }
        }, new DefaultResponseStatusHandler() {
            @Override
            public void statusHandle(HttpAndroidDataSource dataSource, HttpUriRequest request, HttpResponse response) throws IOStatusException, ParseException, IOException {
                if (response.getStatusLine().getStatusCode() == 403) {
                    throw new FeedlyAuthException();
                }
                super.statusHandle(dataSource, request, response);
            }
        });
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
